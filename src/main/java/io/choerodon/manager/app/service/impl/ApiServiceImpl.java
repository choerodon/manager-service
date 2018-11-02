package io.choerodon.manager.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.manager.infra.dataobject.RouteDO;
import io.choerodon.manager.infra.dataobject.SwaggerDO;
import io.choerodon.manager.infra.mapper.RouteMapper;
import io.choerodon.manager.infra.mapper.SwaggerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author superlee
 */
@Service
public class ApiServiceImpl implements ApiService {

    private static final Logger logger = LoggerFactory.getLogger(ApiServiceImpl.class);

    private static final String DESCRIPTION = "description";

    private IDocumentService iDocumentService;

    private SwaggerMapper swaggerMapper;

    private RouteMapper routeMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApiServiceImpl(IDocumentService iDocumentService, SwaggerMapper swaggerMapper, RouteMapper routeMapper) {
        this.swaggerMapper = swaggerMapper;
        this.iDocumentService = iDocumentService;
        this.routeMapper = routeMapper;
    }

    @Override
    public Page<ControllerDTO> getControllers(String name, String version, PageRequest pageRequest, Map<String, Object> map) {
        String serviceName = getRouteName(name);
        String json = getSwaggerJson(name, version, serviceName);
        return Optional.ofNullable(json)
                .map(j -> ManualPageHelper.postPage(processJson2ControllerDTO(name, j), pageRequest, map))
                .orElseThrow(() -> new CommonException("error.service.swaggerJson.empty"));
    }

    private String getSwaggerJson(String name, String version, String serviceName) {
        String json;
        SwaggerDO swaggerDO = new SwaggerDO();
        swaggerDO.setServiceName(serviceName);
        swaggerDO.setServiceVersion(version);
        long start = System.currentTimeMillis();
        SwaggerDO swagger = swaggerMapper.selectOne(swaggerDO);
        long end = System.currentTimeMillis();
        logger.info("%%% select swagger spending {} ms", end - start);
        if (swagger != null && !StringUtils.isEmpty(swagger.getValue())) {
            json = swagger.getValue();
        } else {
            try {
                long start1 = System.currentTimeMillis();
                json = iDocumentService.getSwaggerJson(name, version);
                long end1 = System.currentTimeMillis();
                logger.info("%%% fetch swagger json spending {} ms", end1 - start1);
            } catch (IOException e) {
                logger.error("fetch swagger json error, service: {}, version: {}, exception: {}", name, version, e.getMessage());
                throw new CommonException("error.service.not.run", name, version);
            }
            long start1 = System.currentTimeMillis();
            if (swagger == null) {
                SwaggerDO insertSwagger = new SwaggerDO();
                insertSwagger.setServiceName(serviceName);
                insertSwagger.setServiceVersion(version);
                insertSwagger.setDefault(false);
                insertSwagger.setValue(json);
                if (swaggerMapper.insertSelective(insertSwagger) != 1) {
                    logger.warn("insert swagger error, swagger : {}", insertSwagger.toString());
                }
            } else {
                swagger.setValue(json);
                if (swaggerMapper.updateByPrimaryKeySelective(swagger) != 1) {
                    logger.warn("update swagger error, swagger : {}", swagger.toString());
                }
            }
            long end1 = System.currentTimeMillis();
            logger.info("insert or update json spending {} ms", end1 - start1);

        }
        return json;
    }

    private String getRouteName(String name) {
        long start = System.currentTimeMillis();
        String serviceName;
        RouteDO routeDO = new RouteDO();
        routeDO.setName(name);
        RouteDO route = routeMapper.selectOne(routeDO);
        if (route == null) {
            throw new CommonException("error.route.not.found.routeName{" + name + "}");
        } else {
            serviceName = route.getServiceId();
        }
        long end = System.currentTimeMillis();
        logger.info("%%% get route spending {} ms", end - start);
        return serviceName;
    }

    @Override
    public ControllerDTO queryPathDetail(String name, String version, String controllerName, String operationId) {
        try {
            String serviceName = getRouteName(name);
            String json = getSwaggerJson(name, version, serviceName);
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
        logger.info("%%% process json spending {} ms", end - start);
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
        setCodeOfPathIfExists(serviceName, path, jsonNode.get("description"), tagNode);

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

//    private String addIndent2Comments(String str) {
//        String regex = "\\n//\\S+\\n";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(str);
//        if (matcher.find()) {
//            String targetStr = matcher.group();
//            int start = matcher.start();
//            int end = matcher.end();
//            StringBuilder sb = new StringBuilder();
//            String prefix = str.substring(0, start);
//            sb.append(prefix);
//            sb.append("\n");
//            sb.append(appendIndent(targetStr, prefix));
//            String suffix = str.substring(end, str.length());
//            sb.append(suffix);
//            str = addIndent2Comments(sb.toString());
//        }
//        return str;
//    }

//    private String appendIndent(String targetStr, String prefix) {
//        String comment = targetStr.substring(3, targetStr.length()-1);
//        //计算有几个缩进
//        int a = count(prefix,"\\[");
//        int b = count(prefix,"\\{");
//        int c = count(prefix,"\\]");
//        int d = count(prefix,"\\}");
//        int num = a + b - c - d;
//        StringBuilder sb = new StringBuilder();
//        for(int i = 0; i< num; i++) {
//            sb.append("  ");
//        }
//        sb.append("//");
//        sb.append(comment);
//        sb.append("\n");
//        return sb.toString();
//    }

//    private int count(String prefix, String regex) {
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(prefix);
//        int count = 0;
//        while (matcher.find()) {
//            count ++;
//        }
//        return count;
//    }

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
