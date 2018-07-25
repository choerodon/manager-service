package io.choerodon.manager.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.swagger.*;
import io.choerodon.manager.app.service.ApiService;
import io.choerodon.manager.domain.service.IDocumentService;
import io.choerodon.manager.infra.common.utils.ManualPageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author superlee
 */
@Service
public class ApiServiceImpl implements ApiService {

    private static final Logger logger = LoggerFactory.getLogger(ApiServiceImpl.class);

    private static final String DESCRIPTION = "description";

    private IDocumentService iDocumentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApiServiceImpl (IDocumentService iDocumentService) {
        this.iDocumentService = iDocumentService;
    }

    @Override
    public Page<ControllerDTO> getControllers(String name, String version, PageRequest pageRequest, Map<String, Object> map) {
        String json;
        try {
            json = iDocumentService.getSwaggerJson(name, version);
        } catch (IOException e) {
            logger.error("fetch swagger json error, service: {}, version: {}, exception: {}", name, version, e.getMessage());
            throw new CommonException("error.service.not.run", name, version);
        }
        return Optional.ofNullable(json)
                .map(j -> ManualPageHelper.postPage(processJson2ControllerDTO(j), pageRequest, map))
                .orElseThrow(() -> new CommonException("error.service.swaggerJson.empty"));
    }

    private List<ControllerDTO> processJson2ControllerDTO(String json) {
        List<ControllerDTO> controllers;
        try {
            JsonNode node = objectMapper.readTree(json);
            //解析definitions,构造json
            Map<String, Map<String, FieldDTO>> map = processDefinitions(node);
            Map<String, String> dtoMap = convertMap2JsonWithComments(map);
            controllers = processControllers(node);
            processPaths(node, controllers, dtoMap);
        } catch (IOException e) {
            throw new CommonException("error.parseJson");
        }
        return controllers;
    }

    private Map<String,String> convertMap2JsonWithComments(Map<String, Map<String, FieldDTO>> map) {
        Map<String, String> returnMap = new HashMap<>();
        for (Map.Entry<String, Map<String, FieldDTO>> entry : map.entrySet() ) {
            StringBuilder sb = new StringBuilder();
            String className = entry.getKey();
            process2String(className, map, sb);
            returnMap.put(className, sb.toString());
        }
        return returnMap;
    }

    private void process2String(String ref, Map<String, Map<String, FieldDTO>> map, StringBuilder sb) {
        for (Map.Entry<String, Map<String, FieldDTO>> entry : map.entrySet()) {
            String className = subString4ClassName(ref);
            if (className.equals(entry.getKey())) {
                sb.append("{\n");
                Map<String, FieldDTO> fileds = entry.getValue();
                //两个空格为缩进单位
                for (Map.Entry<String, FieldDTO> entry1 : fileds.entrySet()) {
                    String field = entry1.getKey();
                    FieldDTO dto = entry1.getValue();
                    //如果是集合类型，注释拼到字段的上一行
                    String type = dto.getType();
                    if ("array".equals(type)) {
                        if (dto.getComment() != null) {
                            sb.append("//");
                            sb.append(dto.getComment());
                            sb.append("\n");
                        }
                        appendField(sb, field);
                        sb.append("[\n");
                        if (dto.getRef() != null) {
                            //例外自引用，如果自引用直接返回{}
                            String refClassName = subString4ClassName(dto.getRef());
                            if (className.equals(refClassName)) {
                                sb.append("{}");
                            } else {
                                //递归解析
                                process2String(dto.getRef(), map, sb);
                            }
                        } else {
                            sb.append(type);
                            sb.append("\n");
                        }
                        sb.append("]\n");

                    } else {
                        appendField(sb, field);
                        if ("integer".equals(type) || "string".equals(type) || "boolean".equals(type)) {
                            sb.append("\"");
                            sb.append(type);
                            sb.append("\"");
                            //拼注释
                            appendComment(sb, dto);
                            sb.append("\n");
                        }
                        if ("object".equals(type)) {
                            sb.append("\"{}\"");
                            //拼注释
                            appendComment(sb, dto);
                            sb.append("\n");
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
        JsonNode definitionNodes = node.get("definitions");
        Iterator<String> classNameIterator = definitionNodes.fieldNames();
        while (classNameIterator.hasNext()) {
            String className = classNameIterator.next();
            JsonNode jsonNode = definitionNodes.get(className);
            JsonNode propertyNode = jsonNode.get("properties");
            Iterator<String> filedNameIterator = propertyNode.fieldNames();
            Map<String, FieldDTO> fieldMap = new HashMap<>();
            while (filedNameIterator.hasNext()) {
                FieldDTO field = new FieldDTO();
                String filedName = filedNameIterator.next();
                String type = Optional.ofNullable(propertyNode.get(filedName).get("type")).map(JsonNode::asText).orElse(null);
                field.setType(type);
                String description = Optional.ofNullable(propertyNode.get(filedName).get(DESCRIPTION)).map(JsonNode::asText).orElse(null);
                field.setComment(description);
                JsonNode itemNode = propertyNode.get(filedName).get("items");
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
        return map;
    }

    private void processPaths(JsonNode node, List<ControllerDTO> controllers, Map<String, String> controllerMaps) {
        JsonNode pathNode = node.get("paths");
        Iterator<String> urlIterator = pathNode.fieldNames();
        while (urlIterator.hasNext()) {
            String url = urlIterator.next();
            JsonNode methodNode = pathNode.get(url);
            Iterator<String> methodIterator = methodNode.fieldNames();
            while (methodIterator.hasNext()) {
                PathDTO path = new PathDTO();
                path.setUrl(url);
                String method = methodIterator.next();
                path.setMethod(method);
                JsonNode jsonNode = methodNode.findValue(method);
                JsonNode tagNode = jsonNode.get("tags");
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
                path.setOperationId(jsonNode.get("operationId").asText());
                processConsumes(path, jsonNode);
                processProduces(path, jsonNode);
                processResponses(path, jsonNode, controllerMaps);
                processParameters(path, jsonNode, controllerMaps);
            }
        }
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
                        StringBuilder sb = new StringBuilder();
                        if ("array".equals(type)) {
                            sb.append("[\n");
                            sb.append(body);
                            sb.append("\n]");
                        } else {
                            sb.append(body);
                        }
                        //给array前面的注释加上缩进，即满足\n//\\S+\n的注释
                        response.setBody(addIndent2Comments(sb.toString()));
                    }
                }
            }
            responses.add(response);
        }
        path.setResponses(responses);
    }

    private String addIndent2Comments(String str) {
        String regex = "\\n//\\S+\\n";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            String targetStr = matcher.group();
            int start = matcher.start();
            int end = matcher.end();
            StringBuilder sb = new StringBuilder();
            String prefix = str.substring(0, start);
            sb.append(prefix);
            sb.append(appendIndent(targetStr, prefix));
            String suffix = str.substring(end, str.length());
            sb.append(suffix);
            str = addIndent2Comments(sb.toString());
        }
        return str;
    }

    private String appendIndent(String targetStr, String prefix) {
        String comment = targetStr.substring(3, targetStr.length()-1);
        //计算有几个缩进
        int a = count(prefix,"\\[");
        int b = count(prefix,"\\{");
        int c = count(prefix,"\\]");
        int d = count(prefix,"\\}");
        int num = a + b - c - d;
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for(int i = 0; i< num; i++) {
            sb.append("  ");
        }
        sb.append("//");
        sb.append(comment);
        sb.append("\n");
        return sb.toString();
    }

    private int count(String prefix, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(prefix);
        int count = 0;
        while (matcher.find()) {
            count ++;
        }
        return count;
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
                    Map<String, String> schema = parameter.getSchema();
                    if ("body".equals(parameter.getIn()) && schema != null && !schema.isEmpty()) {
                        String ref = schema.get("$ref");
                        if (ref != null) {
                            for (Map.Entry<String, String> entry : controllerMaps.entrySet()) {
                                String className = subString4ClassName(ref);
                                if (className.equals(entry.getKey())) {
                                    String body = entry.getValue();
                                    parameter.setBody(addIndent2Comments(body));
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
}
