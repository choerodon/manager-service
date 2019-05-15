package io.choerodon.manager.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.choerodon.manager.domain.service.ISwaggerService;
import io.choerodon.manager.infra.dataobject.RouteDO;
import io.choerodon.manager.infra.dataobject.Sort;
import io.choerodon.manager.infra.mapper.RouteMapper;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.MultiKeyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.swagger.PermissionData;
import io.choerodon.core.swagger.SwaggerExtraData;
import io.choerodon.manager.api.dto.swagger.*;
import io.choerodon.manager.app.service.ApiService;
import io.choerodon.manager.domain.manager.entity.MyLinkedList;
import io.choerodon.manager.domain.service.IDocumentService;
import io.choerodon.manager.infra.common.utils.ManualPageHelper;
import springfox.documentation.swagger.web.SwaggerResource;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author superlee
 */
@Service
public class ApiServiceImpl implements ApiService {

    private static final Logger logger = LoggerFactory.getLogger(ApiServiceImpl.class);

    private static final String DESCRIPTION = "description";

    private static final String TITLE = "title";
    private static final String KEY = "key";
    private static final String CHILDREN = "children";
    private static final String API_TREE_DOC = "api-tree-doc";
    private static final String PATH_DETAIL = "path-detail";
    private static final String COLON = ":";
    private static final String UNDERLINE = "-";
    private static final String SERVICE = "service";

    private IDocumentService iDocumentService;

    private RouteMapper routeMapper;

    private ISwaggerService iSwaggerService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Cache<String, String> cache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.DAYS).maximumSize(500).build();

    private StringRedisTemplate redisTemplate;

    public ApiServiceImpl(IDocumentService iDocumentService, RouteMapper routeMapper, ISwaggerService iSwaggerService, StringRedisTemplate redisTemplate) {
        this.iDocumentService = iDocumentService;
        this.routeMapper = routeMapper;
        this.iSwaggerService = iSwaggerService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public PageInfo<ControllerDTO> getControllers(String name, String version, int page, int size, Sort sort, Map<String, Object> map) {
        String json = getSwaggerJson(name, version);
        return Optional.ofNullable(json)
                .map(j -> ManualPageHelper.postPage(processJson2ControllerDTO(name, j), page, size, sort, map))
                .orElseThrow(() -> new CommonException("error.service.swaggerJson.empty"));
    }

    @Override
    public String getSwaggerJson(String name, String version) {
        String serviceName = getRouteName(name);
        String json = iDocumentService.fetchSwaggerJsonByService(serviceName, version);
        try {
            if (json != null) {
                //自定义扩展swaggerJson
                json = iDocumentService.expandSwaggerJson(name, version, json);
            }
        } catch (IOException e) {
            logger.error("fetch swagger json error, service: {}, version: {}, exception: {}", name, version, e.getMessage());
            throw new CommonException(e, "error.service.not.run", name, version);
        }
        return json;
    }

    private MultiKeyMap getInvokeCount(Set<String> paramValues, Set<String> date, LocalDate begin, LocalDate end, String additionKey) {
        MultiKeyMap multiKeyMap = new MultiKeyMap();
        if (begin.isAfter(end)) {
            throw new CommonException("error.date.order");
        }
        while (!begin.isAfter(end)) {
            String currentDate = begin.toString();
            date.add(currentDate);
            String key = getKeyByDateAndAdditionKey(additionKey, currentDate);
            Set<ZSetOperations.TypedTuple<String>> serviceSet = redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);
            serviceSet.forEach(tuple -> {
                paramValues.add(tuple.getValue());
                multiKeyMap.put(key, tuple.getValue(), tuple.getScore());
            });
            begin = begin.plusDays(1);
        }
        return multiKeyMap;
    }

    private String getKeyByDateAndAdditionKey(String additionalKey, String currentDate) {
        StringBuilder builder = new StringBuilder(currentDate).append(COLON).append("zSet");
        if (!StringUtils.isEmpty(additionalKey)) {
            builder.append(COLON).append(additionalKey);
        }
        return builder.toString();
    }

    @Override
    public Map<String, Object> queryServiceInvoke(String beginDate, String endDate) {
        MultiKeyMap multiKeyMap = getServiceMap();
        MapIterator mapIterator = multiKeyMap.mapIterator();
        Set<String> serviceKeySet = new HashSet<>();
        while (mapIterator.hasNext()) {
            MultiKey multiKey = (MultiKey) mapIterator.next();
            Object[] keys = multiKey.getKeys();
            serviceKeySet.add((String) keys[1]);
        }
        return queryInvokeCount(beginDate, endDate, "", SERVICE, serviceKeySet);
    }

    @Override
    public Map<String, Object> queryInvokeCount(String beginDate, String endDate, String additionalKey, String paramKey, Set<String> additionalParamValues) {
        Map<String, Object> map = new HashMap<>();
        Set<String> date = new LinkedHashSet<>();
        List<Map<String, Object>> details = new ArrayList<>();
        map.put("date", date);
        map.put("details", details);
        LocalDate begin = LocalDate.parse(beginDate);
        LocalDate end = LocalDate.parse(endDate);
        Set<String> paramValues = new HashSet<>();
        paramValues.addAll(additionalParamValues);
        MultiKeyMap multiKeyMap = getInvokeCount(paramValues, date, begin, end, additionalKey);
        Map<String, Double> lastDayCount = new HashMap<>();
        paramValues.forEach(paramValue -> {
            Map<String, Object> apiMap = new HashMap<>(2);
            List<Double> data = new ArrayList<>();
            apiMap.put(paramKey, paramValue);
            apiMap.put("data", data);
            date.forEach(currentDate -> {
                String key = getKeyByDateAndAdditionKey(additionalKey, currentDate);
                Object value = multiKeyMap.get(key, paramValue);
                Double count = (value == null ? 0D : (Double) value);
                lastDayCount.put(paramValue, count);
                data.add(count);
            });

            details.add(apiMap);
        });
        List<String> sortedKey =
                lastDayCount.entrySet()
                        .stream()
                        .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
        map.put(paramKey, sortedKey);
        return map;

    }

    @Override
    public Map queryTreeMenu() {
        MultiKeyMap multiKeyMap = getServiceMap();
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        map.put(SERVICE, list);
        MapIterator mapIterator = multiKeyMap.mapIterator();
        while (mapIterator.hasNext()) {
            MultiKey multiKey = (MultiKey) mapIterator.next();
            Object[] keys = multiKey.getKeys();
            String routeName = (String) keys[0];
            String service = (String) keys[1];
            Map<String, Object> serviceMap = new HashMap<>();
            Set<String> versions = (Set<String>) multiKeyMap.get(routeName, service);
            serviceMap.put(TITLE, service);
            List<Map<String, Object>> children = new ArrayList<>();
            serviceMap.put(CHILDREN, children);
            int versionNum = processTreeOnVersionNode(routeName, service, versions, children);
            if (versionNum > 0) {
                list.add(serviceMap);
            }
        }
        processKey(map);
        return map;
    }

    private void processKey(Map<String, Object> map) {
        List<Map<String, Object>> serviceList = (List<Map<String, Object>>) map.get(SERVICE);
        int serviceCount = 0;
        for (Map<String, Object> service : serviceList) {
            String serviceKey = serviceCount + "";
            service.put(KEY, serviceKey);
            List<Map<String, Object>> versions = (List<Map<String, Object>>) service.get(CHILDREN);
            recursion(serviceKey, versions);
            serviceCount++;
        }
    }

    private void recursion(String key, List<Map<String, Object>> list) {
        int count = 0;
        for (Map<String, Object> map : list) {
            String mapKey = new StringBuilder(key).append(UNDERLINE).append(count).toString();
            map.put(KEY, mapKey);
            if (map.get(CHILDREN) != null) {
                List<Map<String, Object>> children = (List<Map<String, Object>>) map.get(CHILDREN);
                recursion(mapKey, children);
            }
            count++;
        }
    }

    private int processTreeOnVersionNode(String routeName, String service, Set<String> versions, List<Map<String, Object>> children) {
        int versionNum = versions.size();
        for (String version : versions) {
            boolean legalVersion = false;
            Map<String, Object> versionMap = new HashMap<>();
            versionMap.put(TITLE, version);
            List<Map<String, Object>> versionChildren = new ArrayList<>();
            versionMap.put(CHILDREN, versionChildren);
            String apiTreeDocKey = getApiTreeDocKey(service, version);
            if (cache.getIfPresent(apiTreeDocKey) != null) {
                String childrenStr = cache.getIfPresent(apiTreeDocKey);
                try {
                    List<Map<String, Object>> list =
                            objectMapper.readValue(childrenStr, new TypeReference<List<Map<String, Object>>>() {
                            });
                    versionChildren.addAll(list);
                    legalVersion = true;
                } catch (IOException e) {
                    logger.error("object mapper read redis cache value {} to List<Map<String, Object>> error, so process children version from db or swagger, exception: {} ", childrenStr, e);
                    legalVersion = processChildrenFromSwaggerJson(routeName, service, version, versionChildren);
                }
            } else {
                legalVersion = processChildrenFromSwaggerJson(routeName, service, version, versionChildren);
            }
            if (legalVersion) {
                children.add(versionMap);
            } else {
                versionNum--;
            }
        }
        return versionNum;
    }

    private boolean processChildrenFromSwaggerJson(String routeName, String service, String version, List<Map<String, Object>> versionChildren) {
        boolean done = false;
        try {
            String json = iDocumentService.fetchSwaggerJsonByService(service, version);
            if (StringUtils.isEmpty(json)) {
                logger.warn("the swagger json of service {} version {} is empty, skip", service, version);
            } else {
                JsonNode node = objectMapper.readTree(json);
                processTreeOnControllerNode(routeName, service, version, node, versionChildren);
            }
            done = true;
        } catch (IOException e) {
            logger.error("object mapper read tree error, service: {}, version: {}", service, version);
        } catch (RemoteAccessException e) {
            logger.error(e.getMessage());
        }
        return done;
    }

    private void processTreeOnControllerNode(String routeName, String service, String version, JsonNode node, List<Map<String, Object>> children) {
        Map<String, Map> controllerMap = processControllerMap(node);
        Map<String, List> pathMap = processPathMap(routeName, service, version, node);
        for (Map.Entry<String, Map> entry : controllerMap.entrySet()) {
            String controllerName = entry.getKey();
            Map<String, Object> controller = entry.getValue();
            List<Map<String, Object>> controllerChildren = (List<Map<String, Object>>) controller.get(CHILDREN);
            List<Map<String, Object>> list = pathMap.get(controllerName);
            if (list != null) {
                children.add(controller);
                for (Map<String, Object> path : list) {
                    path.put("refController", controllerName);
                    controllerChildren.add(path);
                }
            }
        }
        try {
            String key = getApiTreeDocKey(service, version);
            String value = objectMapper.writeValueAsString(children);
            cache.put(key, value);
        } catch (JsonProcessingException e) {
            logger.warn("read object to string error while caching to redis, exception: {}", e);
        }
    }

    private void cache2Redis(String key, Object value) {
        try {
            //缓存10天
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value), 10, TimeUnit.DAYS);
        } catch (JsonProcessingException e) {
            logger.warn("read object to string error while caching to redis, exception: {}", e);
        }
    }

    private String getApiTreeDocKey(String service, String version) {
        StringBuilder stringBuilder = new StringBuilder(API_TREE_DOC);
        stringBuilder.append(COLON).append(service).append(COLON).append(version);
        return stringBuilder.toString();
    }

    private Map<String, List> processPathMap(String routeName, String service, String version, JsonNode node) {
        Map<String, List> pathMap = new HashMap<>();
        JsonNode pathNode = node.get("paths");
        Iterator<String> urlIterator = pathNode.fieldNames();
        while (urlIterator.hasNext()) {
            String url = urlIterator.next();
            JsonNode methodNode = pathNode.get(url);
            Iterator<String> methodIterator = methodNode.fieldNames();
            while (methodIterator.hasNext()) {
                String method = methodIterator.next();
                JsonNode jsonNode = methodNode.findValue(method);
                if (jsonNode.get(DESCRIPTION) == null) {
                    continue;
                }
                Map<String, Object> path = new HashMap<>();
                path.put(TITLE, url);
                path.put("method", method);
                path.put("operationId", Optional.ofNullable(jsonNode.get("operationId")).map(JsonNode::asText).orElse(null));
                path.put(SERVICE, service);
                path.put("version", version);
                path.put(DESCRIPTION, Optional.ofNullable(jsonNode.get("summary")).map(JsonNode::asText).orElse(null));
                path.put("servicePrefix", routeName);
                JsonNode tagNode = jsonNode.get("tags");
                for (int i = 0; i < tagNode.size(); i++) {
                    String tag = tagNode.get(i).asText();
                    if (pathMap.get(tag) == null) {
                        List<Map<String, Object>> list = new ArrayList<>();
                        list.add(path);
                        pathMap.put(tag, list);
                    } else {
                        pathMap.get(tag).add(path);
                    }
                }
            }
        }
        return pathMap;
    }

    private Map<String, Map> processControllerMap(JsonNode node) {
        Map<String, Map> controllerMap = new HashMap<>();
        JsonNode tagNodes = node.get("tags");
        Iterator<JsonNode> iterator = tagNodes.iterator();
        while (iterator.hasNext()) {
            JsonNode jsonNode = iterator.next();
            String name = jsonNode.findValue("name").asText();
            if (!name.contains("-controller") && !name.contains("-endpoint")) {
                continue;
            }
            Map<String, Object> controller = new HashMap<>();
            controllerMap.put(name, controller);
            controller.put(TITLE, name);
            List<Map<String, Object>> controllerChildren = new ArrayList<>();
            controller.put(CHILDREN, controllerChildren);
        }
        return controllerMap;
    }

    /**
     * @return MultiKeyMap, key1 is route name, key2 is service id, value is version set
     */
    private MultiKeyMap getServiceMap() {
        MultiKeyMap multiKeyMap = new MultiKeyMap();
        List<SwaggerResource> resources = iSwaggerService.getSwaggerResource();
        for (SwaggerResource resource : resources) {
            String name = resource.getName();
            String[] nameArray = name.split(COLON);
            String location = resource.getLocation();
            String[] locationArray = location.split("\\?version=");
            if (nameArray.length != 2 || locationArray.length != 2) {
                logger.warn("the resource name is not match xx:xx or location is not match /doc/xx?version=xxx , name : {}, location: {}", name, location);
                continue;
            }
            String routeName = nameArray[0];
            String service = nameArray[1];
            String version = locationArray[1];
            if (multiKeyMap.get(routeName, service) == null) {
                Set<String> versionSet = new HashSet<>();
                versionSet.add(version);
                multiKeyMap.put(routeName, service, versionSet);
            } else {
                Set<String> versionSet = (Set<String>) multiKeyMap.get(routeName, service);
                versionSet.add(version);
            }
        }
        return multiKeyMap;
    }

    private String getRouteName(String name) {
        String serviceName;
        RouteDO routeDO = new RouteDO();
        routeDO.setName(name);
        RouteDO route = routeMapper.selectOne(routeDO);
        if (route == null) {
            throw new CommonException("error.route.not.found.routeName{" + name + "}");
        } else {
            serviceName = route.getServiceId();
        }
        return serviceName;
    }

    @Override
    public ControllerDTO queryPathDetail(String serviceName, String version, String controllerName, String operationId) {
        String key = getPathDetailRedisKey(serviceName, version, controllerName, operationId);
        if (redisTemplate.hasKey(key)) {
            String value = redisTemplate.opsForValue().get(key);
            try {
                return objectMapper.readValue(value, ControllerDTO.class);
            } catch (IOException e) {
                logger.error("object mapper read redis cache value {} to ControllerDTO error, so process from db or swagger, exception: {} ", value, e);
            }
        }
        try {
            return processPathDetailFromSwagger(serviceName, version, controllerName, operationId, key);
        } catch (IOException e) {
            logger.error("fetch swagger json error, service: {}, version: {}, exception: {}", serviceName, version, e.getMessage());
            throw new CommonException("error.service.not.run", serviceName, version);
        }

    }

    private ControllerDTO processPathDetailFromSwagger(String name, String version, String controllerName, String operationId, String key) throws IOException {
        String json = getSwaggerJson(name, version);
        JsonNode node = objectMapper.readTree(json);
        List<ControllerDTO> controllers = processControllers(node);
        List<ControllerDTO> targetControllers =
                controllers.stream().filter(c -> controllerName.equals(c.getName())).collect(Collectors.toList());
        if (targetControllers.isEmpty()) {
            throw new CommonException("error.controller.not.found", controllerName);
        }
        Map<String, Map<String, FieldDTO>> map = processDefinitions(node);
        Map<String, String> dtoMap = convertMap2JsonWithComments(map);
        JsonNode pathNode = node.get("paths");
        String basePath = node.get("basePath").asText();
        ControllerDTO controller = queryPathDetailByOptions(name, pathNode, targetControllers, operationId, dtoMap, basePath);
        cache2Redis(key, controller);
        return controller;
    }

    private String getPathDetailRedisKey(String name, String version, String controllerName, String operationId) {
        StringBuilder builder = new StringBuilder(PATH_DETAIL);
        builder
                .append(COLON)
                .append(name)
                .append(COLON)
                .append(version)
                .append(COLON)
                .append(controllerName)
                .append(COLON)
                .append(operationId);
        return builder.toString();
    }

    @Override
    public Map<String, Object> queryInstancesAndApiCount() {
        Map<String, Object> apiCountMap = new HashMap<>(2);
        List<String> services = new ArrayList<>();
        List<Integer> apiCounts = new ArrayList<>();
        apiCountMap.put("services", services);
        apiCountMap.put("apiCounts", apiCounts);
        MultiKeyMap multiKeyMap = getServiceMap();
        MapIterator mapIterator = multiKeyMap.mapIterator();
        while (mapIterator.hasNext()) {
            MultiKey multiKey = (MultiKey) mapIterator.next();
            Object[] keys = multiKey.getKeys();
            String routeName = (String) keys[0];
            String service = (String) keys[1];
            Set<String> versions = (Set<String>) multiKeyMap.get(routeName, service);
            int count = 0;
            //目前只有一个版本，所以取第一个，如果后续支持多版本，此处遍历版本即可
            Iterator<String> iterator = versions.iterator();
            String version = null;
            while (iterator.hasNext()) {
                version = iterator.next();
                break;
            }
            boolean done = false;
            if (version != null) {
                try {
                    String json = iDocumentService.fetchSwaggerJsonByService(service, version);
                    if (StringUtils.isEmpty(json)) {
                        logger.warn("the swagger json of service {} version {} is empty, skip", service, version);
                    } else {
                        JsonNode node = objectMapper.readTree(json);
                        JsonNode pathNode = node.get("paths");
                        Iterator<String> urlIterator = pathNode.fieldNames();
                        while (urlIterator.hasNext()) {
                            String url = urlIterator.next();
                            JsonNode methodNode = pathNode.get(url);
                            count = count + methodNode.size();
                        }
                        done = true;
                    }
                } catch (IOException e) {
                    logger.error("object mapper read tree error, service: {}, version: {}", service, version);
                } catch (RemoteAccessException e) {
                    logger.error(e.getMessage());
                }
            }
            if (done) {
                services.add(service);
                apiCounts.add(count);
            }
        }
        return apiCountMap;
    }

    private List<ControllerDTO> processJson2ControllerDTO(String serviceName, String json) {
        List<ControllerDTO> controllers;
        try {
            JsonNode node = objectMapper.readTree(json);
            //解析definitions,构造json
            String basePath = node.get("basePath").asText();
            Map<String, Map<String, FieldDTO>> map = processDefinitions(node);
            Map<String, String> dtoMap = convertMap2JsonWithComments(map);
            controllers = processControllers(node);
            JsonNode pathNode = node.get("paths");
            processPaths(serviceName, pathNode, controllers, dtoMap, basePath);
        } catch (IOException e) {
            throw new CommonException("error.parseJson");
        }
        return controllers;
    }

    private Map<String, String> convertMap2JsonWithComments(Map<String, Map<String, FieldDTO>> map) {
        Map<String, String> returnMap = new HashMap<>();
        for (Map.Entry<String, Map<String, FieldDTO>> entry : map.entrySet()) {
            StringBuilder sb = new StringBuilder();
            String className = entry.getKey();
            //dto引用链表，用于判断是否有循环引用
            MyLinkedList<String> linkedList = new MyLinkedList<>();
            linkedList.addNode(className);
            process2String(className, map, sb, linkedList);
            returnMap.put(className, sb.toString());
        }
        return returnMap;
    }

    private void process2String(String ref, Map<String, Map<String, FieldDTO>> map, StringBuilder sb, MyLinkedList<String> linkedList) {
        for (Map.Entry<String, Map<String, FieldDTO>> entry : map.entrySet()) {
            String className = subString4ClassName(ref);
            if (className.equals(entry.getKey())) {
                sb.append("{\n");
                Map<String, FieldDTO> fileds = entry.getValue();
                //两个空格为缩进单位
                if (fileds != null) {
                    for (Map.Entry<String, FieldDTO> entry1 : fileds.entrySet()) {
                        String field = entry1.getKey();
                        FieldDTO dto = entry1.getValue();
                        //如果是集合类型，注释拼到字段的上一行
                        String type = dto.getType();
                        if ("array".equals(type)) {
                            //处理集合引用的情况，type为array
                            if (dto.getComment() != null) {
                                sb.append("//");
                                sb.append(dto.getComment());
                                sb.append("\n");
                            }
                            appendField(sb, field);
                            sb.append("[\n");
                            if (dto.getRef() != null) {
                                String refClassName = subString4ClassName(dto.getRef());
                                //linkedList深拷贝一份，处理同一个对象对另一个对象的多次引用的情况
                                MyLinkedList<String> copyLinkedList = linkedList.deepCopy();
                                copyLinkedList.addNode(refClassName);
                                //循环引用直接跳出递归
                                if (copyLinkedList.isLoop()) {
                                    sb.append("{}");
                                } else {
                                    //递归解析
                                    process2String(refClassName, map, sb, copyLinkedList);
                                }
                            } else {
                                sb.append(type);
                                sb.append("\n");
                            }
                            sb.append("]\n");
                        } else if (StringUtils.isEmpty(type)) {
                            //单一对象引用的情况，只有ref
                            if (dto.getRef() != null) {
                                if (dto.getComment() != null) {
                                    sb.append("//");
                                    sb.append(dto.getComment());
                                    sb.append("\n");
                                }
                                appendField(sb, field);
                                String refClassName = subString4ClassName(dto.getRef());
                                //linkedList深拷贝一份，处理同一个对象对另一个对象的多次引用的情况
                                MyLinkedList<String> copyLinkedList = linkedList.deepCopy();
                                copyLinkedList.addNode(refClassName);
                                //循环引用直接跳出递归
                                if (copyLinkedList.isLoop()) {
                                    sb.append("{}");
                                } else {
                                    //递归解析
                                    process2String(refClassName, map, sb, copyLinkedList);
                                }
                            } else {
                                sb.append("{}\n");
                            }
                        } else {
                            if ("integer".equals(type) || "string".equals(type) || "boolean".equals(type)) {
                                appendField(sb, field);
                                sb.append("\"");
                                sb.append(type);
                                sb.append("\"");
                                //拼注释
                                appendComment(sb, dto);
                                sb.append("\n");
                            }
                            if ("object".equals(type)) {
                                appendField(sb, field);
                                sb.append("\"{}\"");
                                //拼注释
                                appendComment(sb, dto);
                                sb.append("\n");
                            }
                        }
                    }
                }
                sb.append("}");
            }
        }
    }

    private String subString4ClassName(String ref) {
        //截取#/definitions/RouteDTO字符串，拿到类名
        String[] arr = ref.split("/");
        return arr[arr.length - 1];
    }

    private void appendField(StringBuilder sb, String field) {
        sb.append("\"");
        sb.append(field);
        sb.append("\"");
        sb.append(COLON);
    }

    private void appendComment(StringBuilder sb, FieldDTO dto) {
        if (dto.getComment() != null) {
            sb.append(" //");
            sb.append(dto.getComment());
        }
    }

    private List<ControllerDTO> processControllers(JsonNode node) {
        List<ControllerDTO> controllers = new ArrayList<>();
        JsonNode tagNodes = node.get("tags");
        Iterator<JsonNode> iterator = tagNodes.iterator();
        while (iterator.hasNext()) {
            JsonNode jsonNode = iterator.next();
            String name = jsonNode.findValue("name").asText();
            String description = jsonNode.findValue(DESCRIPTION).asText();
            ControllerDTO controller = new ControllerDTO();
            controller.setName(name);
            controller.setDescription(description);
            controller.setPaths(new ArrayList<>());
            controllers.add(controller);
        }
        return controllers;
    }

    private Map<String, Map<String, FieldDTO>> processDefinitions(JsonNode node) {
        Map<String, Map<String, FieldDTO>> map = new HashMap<>();
        //definitions节点是controller里面的对象json集合
        JsonNode definitionNodes = node.get("definitions");
        if (definitionNodes != null) {
            Iterator<String> classNameIterator = definitionNodes.fieldNames();
            while (classNameIterator.hasNext()) {
                String className = classNameIterator.next();
                JsonNode jsonNode = definitionNodes.get(className);
                JsonNode propertyNode = jsonNode.get("properties");
                if (propertyNode == null) {
                    String type = jsonNode.get("type").asText();
                    if ("object".equals(type)) {
                        map.put(className, null);
                    }
                    continue;
                }
                Iterator<String> filedNameIterator = propertyNode.fieldNames();
                Map<String, FieldDTO> fieldMap = new HashMap<>();
                while (filedNameIterator.hasNext()) {
                    FieldDTO field = new FieldDTO();
                    String filedName = filedNameIterator.next();
                    JsonNode fieldNode = propertyNode.get(filedName);
                    String type = Optional.ofNullable(fieldNode.get("type")).map(JsonNode::asText).orElse(null);
                    field.setType(type);
                    String description = Optional.ofNullable(fieldNode.get(DESCRIPTION)).map(JsonNode::asText).orElse(null);
                    field.setComment(description);
                    field.setRef(Optional.ofNullable(fieldNode.get("$ref")).map(JsonNode::asText).orElse(null));
                    JsonNode itemNode = fieldNode.get("items");
                    Optional.ofNullable(itemNode).ifPresent(i -> {
                        if (i.get("type") != null) {
                            field.setItemType(i.get("type").asText());
                        }
                        if (i.get("$ref") != null) {
                            field.setRef(i.get("$ref").asText());
                        }
                    });
                    fieldMap.put(filedName, field);
                }
                map.put(className, fieldMap);
            }
        }
        return map;
    }

    private ControllerDTO queryPathDetailByOptions(String serviceName, JsonNode pathNode, List<ControllerDTO> targetControllers, String operationId,
                                                   Map<String, String> dtoMap, String basePath) {
        Iterator<String> urlIterator = pathNode.fieldNames();
        while (urlIterator.hasNext()) {
            String url = urlIterator.next();
            JsonNode methodNode = pathNode.get(url);
            Iterator<String> methodIterator = methodNode.fieldNames();
            while (methodIterator.hasNext()) {
                String method = methodIterator.next();
                JsonNode pathDetailNode = methodNode.get(method);
                String pathOperationId = pathDetailNode.get("operationId").asText();
                if (operationId.equals(pathOperationId)) {
                    processPathDetail(serviceName, targetControllers, dtoMap, url, methodNode, method, basePath);
                }
            }
        }
        return targetControllers.get(0);
    }

    private void processPaths(String serviceName, JsonNode pathNode, List<ControllerDTO> controllers, Map<String, String> dtoMap, String basePath) {
        Iterator<String> urlIterator = pathNode.fieldNames();
        while (urlIterator.hasNext()) {
            String url = urlIterator.next();
            JsonNode methodNode = pathNode.get(url);
            Iterator<String> methodIterator = methodNode.fieldNames();
            while (methodIterator.hasNext()) {
                String method = methodIterator.next();
                processPathDetail(serviceName, controllers, dtoMap, url, methodNode, method, basePath);
            }
        }
    }

    private void processPathDetail(String serviceName, List<ControllerDTO> controllers, Map<String, String> dtoMap,
                                   String url, JsonNode methodNode, String method, String basePath) {
        PathDTO path = new PathDTO();
        path.setBasePath(basePath);
        path.setUrl(url);
        path.setMethod(method);
        JsonNode jsonNode = methodNode.findValue(method);
        JsonNode tagNode = jsonNode.get("tags");

        path.setInnerInterface(false);
        setCodeOfPathIfExists(serviceName, path, jsonNode.get(DESCRIPTION), tagNode);

        for (int i = 0; i < tagNode.size(); i++) {
            String tag = tagNode.get(i).asText();
            controllers.forEach(c -> {
                List<PathDTO> paths = c.getPaths();
                if (tag.equals(c.getName())) {
                    path.setRefController(c.getName());
                    paths.add(path);
                }
            });
        }
        path.setRemark(Optional.ofNullable(jsonNode.get("summary")).map(JsonNode::asText).orElse(null));
        path.setDescription(Optional.ofNullable(jsonNode.get(DESCRIPTION)).map(JsonNode::asText).orElse(null));
        path.setOperationId(Optional.ofNullable(jsonNode.get("operationId")).map(JsonNode::asText).orElse(null));
        processConsumes(path, jsonNode);
        processProduces(path, jsonNode);
        processResponses(path, jsonNode, dtoMap);
        processParameters(path, jsonNode, dtoMap);
    }

    private void processResponses(PathDTO path, JsonNode jsonNode, Map<String, String> controllerMaps) {
        JsonNode responseNode = jsonNode.get("responses");
        List<ResponseDTO> responses = new ArrayList<>();
        Iterator<String> responseIterator = responseNode.fieldNames();
        while (responseIterator.hasNext()) {
            String status = responseIterator.next();
            JsonNode node = responseNode.get(status);
            ResponseDTO response = new ResponseDTO();
            response.setHttpStatus(status);
            response.setDescription(node.get(DESCRIPTION).asText());
            JsonNode schemaNode = node.get("schema");
            if (schemaNode != null) {
                JsonNode refNode = schemaNode.get("$ref");
                if (refNode != null) {
                    for (Map.Entry<String, String> entry : controllerMaps.entrySet()) {
                        String className = subString4ClassName(refNode.asText());
                        if (className.equals(entry.getKey())) {
                            response.setBody(entry.getValue());
                        }
                    }
                } else {
                    String type = Optional.ofNullable(schemaNode.get("type")).map(JsonNode::asText).orElse(null);
                    String ref = Optional.ofNullable(schemaNode.get("items"))
                            .map(itemNode ->
                                    Optional.ofNullable(itemNode.get("$ref"))
                                            .map(JsonNode::asText)
                                            .orElse(null))
                            .orElse(null);
                    if (ref != null) {
                        String body = "";
                        for (Map.Entry<String, String> entry : controllerMaps.entrySet()) {
                            String className = subString4ClassName(ref);
                            if (className.equals(entry.getKey())) {
                                body = entry.getValue();
                            }
                        }
                        StringBuilder sb = arrayTypeAppendBrackets(type, body);
                        //给array前面的注释加上缩进，即满足\n//\\S+\n的注释
                        response.setBody(sb.toString());
                    } else {
                        if ("object".equals(type)) {
                            response.setBody("{}");
                        } else {
                            response.setBody(type);
                        }
                    }
                }
            }
            responses.add(response);
        }
        path.setResponses(responses);
    }

    /**
     * set the code field of the instance of {@link PathDTO} if the extraDataNode parameter
     * is not null
     *
     * @param serviceName   the name of the service
     * @param path          the dto
     * @param extraDataNode the extra data node
     * @param tagNode       the tag node
     */
    private void setCodeOfPathIfExists(String serviceName, PathDTO path, JsonNode extraDataNode, JsonNode tagNode) {
        if (extraDataNode != null) {
            try {
                SwaggerExtraData extraData;
                String resourceCode = null;
                for (int i = 0; i < tagNode.size(); i++) {
                    String tag = tagNode.get(i).asText();
                    if (tag.endsWith("-controller")) {
                        resourceCode = tag.substring(0, tag.length() - "-controller".length());
                    } else if (tag.endsWith("-endpoint")) {
                        resourceCode = tag.substring(0, tag.length() - "-endpoint".length());
                    } else {
                        throw new CommonException("error.illegal.tags");
                    }
                }
                extraData = new ObjectMapper().readValue(extraDataNode.asText(), SwaggerExtraData.class);
                PermissionData permission = extraData.getPermission();
                String action = permission.getAction();
                path.setInnerInterface(permission.isPermissionWithin());
                path.setCode(String.format("%s-service.%s.%s", serviceName, resourceCode, action));
            } catch (IOException e) {
                logger.info("extraData read failed.", e);
            }
        }
    }

    private void processConsumes(PathDTO path, JsonNode jsonNode) {
        JsonNode consumeNode = jsonNode.get("consumes");
        List<String> consumes = new ArrayList<>();
        for (int i = 0; i < consumeNode.size(); i++) {
            consumes.add(consumeNode.get(i).asText());
        }
        path.setConsumes(consumes);
    }

    private void processProduces(PathDTO path, JsonNode jsonNode) {
        JsonNode produceNode = jsonNode.get("produces");
        List<String> produces = new ArrayList<>();
        for (int i = 0; i < produceNode.size(); i++) {
            produces.add(produceNode.get(i).asText());
        }
        path.setProduces(produces);
    }

    private void processParameters(PathDTO path, JsonNode jsonNode, Map<String, String> controllerMaps) {
        JsonNode parameterNode = jsonNode.get("parameters");
        List<ParameterDTO> parameters = new ArrayList<>();
        if (parameterNode != null) {
            for (int i = 0; i < parameterNode.size(); i++) {
                try {
                    ParameterDTO parameter = objectMapper.treeToValue(parameterNode.get(i), ParameterDTO.class);
                    SchemaDTO schema = parameter.getSchema();
                    if ("body".equals(parameter.getIn()) && schema != null) {
                        String ref = schema.getRef();
                        if (ref != null) {
                            for (Map.Entry<String, String> entry : controllerMaps.entrySet()) {
                                String className = subString4ClassName(ref);
                                if (className.equals(entry.getKey())) {
                                    String body = entry.getValue();
                                    parameter.setBody(body);
                                }
                            }
                        } else {
                            String type = schema.getType();
                            String itemRef = Optional.ofNullable(schema.getItems()).map(m -> m.get("$ref")).orElse(null);
                            if (itemRef != null) {
                                String body = "";
                                for (Map.Entry<String, String> entry : controllerMaps.entrySet()) {
                                    String className = subString4ClassName(itemRef);
                                    if (className.equals(entry.getKey())) {
                                        body = entry.getValue();
                                    }
                                }
                                StringBuilder sb = arrayTypeAppendBrackets(type, body);
                                parameter.setBody(sb.toString());
                            } else {
                                if (!"object".equals(type)) {
                                    parameter.setBody(type);
                                } else {
                                    Map<String, String> map = schema.getAdditionalProperties();
                                    if (map != null && "array".equals(map.get("type"))) {
                                        parameter.setBody("[{}]");
                                    } else {
                                        parameter.setBody("{}");
                                    }
                                }
                            }
                        }
                    }
                    parameters.add(parameter);
                } catch (JsonProcessingException e) {
                    logger.info("jsonNode to parameterDTO failed, exception: {}", e.getMessage());
                }
            }
        }
        path.setParameters(parameters);
    }

    private StringBuilder arrayTypeAppendBrackets(String type, String body) {
        StringBuilder sb = new StringBuilder();
        if ("array".equals(type)) {
            sb.append("[\n");
            sb.append(body);
            sb.append("\n]");
        } else {
            sb.append(body);
        }
        return sb;
    }
}
