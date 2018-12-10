package io.choerodon.manager.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.manager.domain.service.ISwaggerService;
import io.choerodon.manager.infra.dataobject.RouteDO;
import io.choerodon.manager.infra.mapper.RouteMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.swagger.PermissionData;
import io.choerodon.core.swagger.SwaggerExtraData;
import io.choerodon.manager.api.dto.swagger.*;
import io.choerodon.manager.app.service.ApiService;
import io.choerodon.manager.domain.manager.entity.MyLinkedList;
import io.choerodon.manager.domain.service.IDocumentService;
import io.choerodon.manager.infra.common.utils.ManualPageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import springfox.documentation.swagger.web.SwaggerResource;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author superlee
 */
@Service
public class ApiServiceImpl implements ApiService {

    private static final Logger logger = LoggerFactory.getLogger(ApiServiceImpl.class);

    private static final String DESCRIPTION = "description";

    private IDocumentService iDocumentService;

    private RouteMapper routeMapper;

    private ISwaggerService iSwaggerService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private StringRedisTemplate redisTemplate;

    public ApiServiceImpl(IDocumentService iDocumentService, RouteMapper routeMapper, ISwaggerService iSwaggerService, StringRedisTemplate redisTemplate) {
        this.iDocumentService = iDocumentService;
        this.routeMapper = routeMapper;
        this.iSwaggerService = iSwaggerService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Page<ControllerDTO> getControllers(String name, String version, PageRequest pageRequest, Map<String, Object> map) {
        String json = getSwaggerJson(name, version);
        return Optional.ofNullable(json)
                .map(j -> ManualPageHelper.postPage(processJson2ControllerDTO(name, j), pageRequest, map))
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

    @Override
    public Map<String, Object> queryServiceInvoke(String beginDate, String endDate) {
        Set<String> keySet = getServiceSet();
        List<Map<String, Object>> details = new ArrayList<>();
        for (String service : keySet) {
            Map<String, Object> detailMap = new HashMap<>(2);
            detailMap.put("service", service);
            detailMap.put("data", new ArrayList<>());
            details.add(detailMap);
        }
        Map<String, Object> map = new HashMap<>();
        Set<String> date = new LinkedHashSet<>();
        map.put("date", date);
        map.put("details", details);
        map.put("services", keySet);
        validateDate(beginDate);
        validateDate(endDate);
        setDetails(beginDate, endDate, details, date);
        return map;
    }

    private void setDetails(String beginDate, String endDate, List<Map<String, Object>> details, Set<String> date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date begin = dateFormat.parse(beginDate);
            Date end = dateFormat.parse(endDate);
            if (begin.after(end)) {
                throw new CommonException("error.date.order");
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(begin);
            while (true) {
                if (calendar.getTime().after(end)) {
                    break;
                }
                String dateStr = dateFormat.format(calendar.getTime());
                date.add(dateStr);
                String value = redisTemplate.opsForValue().get(dateStr);
                if (StringUtils.isEmpty(value)) {
                    details.forEach(m -> ((List<Integer>) m.get("data")).add(0));
                } else {
                    try {
                        Map<String, Integer> serviceMap = objectMapper.readValue(value, new TypeReference<Map<String, Integer>>() {
                        });
                        details.forEach(m -> {
                            String service = (String) m.get("service");
                            List<Integer> list = (List<Integer>) m.get("data");
                            list.add(serviceMap.get(service) == null ? 0 : serviceMap.get(service));
                        });
                    } catch (IOException e) {
                        logger.error("object mapper read value to map error, redis key {}, value {}, exception :: {}", dateStr, value, e);
                    }
                }
                calendar.add(Calendar.DATE, 1);
            }
        } catch (ParseException e) {
            throw new CommonException("error.date.parse", beginDate, endDate);
        }
    }

    @Override
    public Map<String, Object> queryApiInvoke(String beginDate, String endDate, String service) {
        Map<String, Object> map = new HashMap<>();
        Set<String> date = new LinkedHashSet<>();
        List<Map<String, Object>> details = new ArrayList<>();
        Set<String> keySet = new HashSet<>();
        map.put("date", date);
        map.put("details", details);
        map.put("apis", keySet);
        validateDate(beginDate);
        validateDate(endDate);
        Map<String, Map<String, Integer>> dateMap = new HashMap<>();
        setDataMapAndKeySet(beginDate, endDate, service, date, keySet, dateMap);
        for (String api : keySet) {
            Map<String, Object> detailMap = new HashMap<>(2);
            detailMap.put("api", api);
            List<Integer> data = new ArrayList<>();
            detailMap.put("data", data);
            for (String dateStr : date) {
                Map<String, Integer> apiMap = dateMap.get(dateStr);
                if (apiMap == null) {
                    data.add(0);
                } else {
                    data.add(apiMap.get(api) == null ? 0 : apiMap.get(api));
                }
            }
            details.add(detailMap);
        }
        return map;

    }

    private void setDataMapAndKeySet(String beginDate, String endDate, String service, Set<String> date, Set<String> keySet, Map<String, Map<String, Integer>> dateMap) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date begin = dateFormat.parse(beginDate);
            Date end = dateFormat.parse(endDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(begin);
            if (begin.after(end)) {
                throw new CommonException("error.date.order");
            }
            while (true) {
                if (calendar.getTime().after(end)) {
                    break;
                }
                String dateStr = dateFormat.format(calendar.getTime());
                date.add(dateStr);
                String value = redisTemplate.opsForValue().get(dateStr + ":" + service);
                if (StringUtils.isEmpty(value)) {
                    dateMap.put(dateStr, null);
                } else {
                    try {
                        Map<String, Integer> apiMap =
                                objectMapper.readValue(value, new TypeReference<Map<String, Integer>>() {
                                });
                        keySet.addAll(apiMap.keySet());
                        dateMap.put(dateStr, apiMap);
                    } catch (IOException e) {
                        logger.error("object mapper read value to map error, redis key {}, value {}, exception :: {}", dateStr, value, e);
                    }
                }
                calendar.add(Calendar.DATE, 1);
            }
        } catch (ParseException e) {
            throw new CommonException("error.date.parse", beginDate, endDate);
        }
    }

    private Map<String, Object> getApiInvoke(Date begin, Date end, String service, SimpleDateFormat dateFormat, Set<String> apiSet) {
        Map<String, Object> map = new HashMap<>();
        Set<String> date = new LinkedHashSet<>();
        List<Object> details = new ArrayList<>();
        map.put("date", date);
        map.put("details", details);
        map.put("apis", apiSet);
        for (String api : apiSet) {
            Map apiDetailMap = new HashMap();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(begin);
            List<String> data = new ArrayList<>();
            apiDetailMap.put("api", api);
            apiDetailMap.put("data", data);
            String suffixKey = service + ":" + api;
            staticDailyInvoking(end, dateFormat, date, calendar, data, suffixKey);
            details.add(apiDetailMap);
        }
        return map;
    }

    private void staticDailyInvoking(Date end, SimpleDateFormat dateFormat, Set<String> date, Calendar calendar, List<String> data, String suffixKey) {
        while (true) {
            if (calendar.getTime().after(end)) {
                break;
            }
            String dateStr = dateFormat.format(calendar.getTime());
            date.add(dateStr);
            StringBuilder builder = new StringBuilder(dateStr);
            builder.append(":");
            builder.append(suffixKey);
            String key = builder.toString();
            String value = redisTemplate.opsForValue().get(key);
            data.add(value == null ? "0" : value);
            calendar.add(Calendar.DATE, 1);
        }
    }

    private Set<String> getAllApiSet(Date begin, Date end, String service, SimpleDateFormat dateFormat) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(begin);
        Set<String> apisWithDateAndService = new HashSet<>();
        while (true) {
            if (calendar.getTime().after(end)) {
                break;
            }
            String dateStr = dateFormat.format(calendar.getTime());
            StringBuilder builder = new StringBuilder(dateStr);
            builder.append(":");
            builder.append(service);
            builder.append(":*");
            Set<String> set = redisTemplate.keys(builder.toString());
            apisWithDateAndService.addAll(set);
            calendar.add(Calendar.DATE, 1);
        }
        Set<String> apis = new HashSet<>();
        for (String api : apisWithDateAndService) {
            String[] str = api.split(":");
            if (str.length != 4) {
                logger.warn("illegal api : {}, skip", api);
                continue;
            }
            apis.add(str[2] + ":" + str[3]);
        }
        return apis;
    }

    private Set<String> getServiceSet() {
        Set<String> serviceSet = new HashSet<>();
        List<SwaggerResource> resources = iSwaggerService.getSwaggerResource();
        for (SwaggerResource resource : resources) {
            String name = resource.getName();
            String[] nameArray = name.split(":");
            if (nameArray.length != 2) {
                logger.warn("the resource name is not match xx:xx , name : {}", name);
                continue;
            }
            serviceSet.add(nameArray[1]);
        }
        return serviceSet;
    }

    private void validateDate(String date) {
        String dateRegex = "\\d{4}-\\d{2}-\\d{2}";
        if (!Pattern.matches(dateRegex, date)) {
            throw new CommonException("error.date.format");
        }
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
    public ControllerDTO queryPathDetail(String name, String version, String controllerName, String operationId) {
        try {
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
            return queryPathDetailByOptions(name, pathNode, targetControllers, operationId, dtoMap, basePath).get(0);
        } catch (IOException e) {
            logger.error("fetch swagger json error, service: {}, version: {}, exception: {}", name, version, e.getMessage());
            throw new CommonException("error.service.not.run", name, version);
        }
    }

    @Override
    public Map<String, Object> queryInstancesAndApiCount() {
        List<SwaggerResource> swaggerResources = iSwaggerService.getSwaggerResource();
        Map<String, Object> apiCountMap = new HashMap<>(2);
        List<String> services = new ArrayList<>();
        List<String> apiCounts = new ArrayList<>();
        apiCountMap.put("services", services);
        apiCountMap.put("apiCounts", apiCounts);
        swaggerResources.forEach(resource -> {
            int count = 0;
            String name = resource.getName();
            String[] nameArray = name.split(":");
            if (nameArray.length != 2) {
                logger.warn("the resource name is not match xx:xx , name : {}", name);
                return;
            }
            String routeName = nameArray[0];
            String serviceName = nameArray[1];
            String location = resource.getLocation();
            String[] locationArray = location.split("\\?version=");
            if (locationArray.length != 2) {
                logger.warn("the location is not match xx?version=xx , location : {}", location);
                return;
            }
            String version = locationArray[1];
            String json = null;
            try {
                json = getSwaggerJson(routeName, version);
            } catch (Exception e) {
                logger.error("can not fetch service {} version {} swagger json, exception : {} ", serviceName, version, e);
            }
            if (json == null) {
                logger.warn("service {}, version {} has been abandoned because of the swagger json is null", serviceName, version);
                return;
            }
            try {
                JsonNode node = objectMapper.readTree(json);
                JsonNode pathNode = node.get("paths");
                Iterator<String> urlIterator = pathNode.fieldNames();
                while (urlIterator.hasNext()) {
                    String url = urlIterator.next();
                    JsonNode methodNode = pathNode.get(url);
                    count = count + methodNode.size();
                }
            } catch (IOException e) {
                logger.error("objectMapper parse json exception, service {}, version {} has been abandoned, exception : {}", serviceName, version, e);
                return;
            }
            services.add(serviceName);
            apiCounts.add(count + "");
        });
        return apiCountMap;
    }

    private List<ControllerDTO> processJson2ControllerDTO(String serviceName, String json) {
        long start = System.currentTimeMillis();
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
        long end = System.currentTimeMillis();
        logger.info("%%%service {} process json spending {} ms", serviceName, end - start);
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
        sb.append(":");
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

    private List<ControllerDTO> queryPathDetailByOptions(String serviceName, JsonNode pathNode, List<ControllerDTO> targetControllers, String operationId,
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
        return targetControllers;
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
