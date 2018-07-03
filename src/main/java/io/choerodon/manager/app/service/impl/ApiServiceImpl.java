package io.choerodon.manager.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.swagger.ControllerDTO;
import io.choerodon.manager.api.dto.swagger.ParameterDTO;
import io.choerodon.manager.api.dto.swagger.PathDTO;
import io.choerodon.manager.api.dto.swagger.ResponseDTO;
import io.choerodon.manager.app.service.ApiService;
import io.choerodon.manager.domain.service.IDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * @author superlee
 */
@Service
public class ApiServiceImpl implements ApiService {

    private static final Logger logger = LoggerFactory.getLogger(ApiServiceImpl.class);

    private IDocumentService iDocumentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApiServiceImpl (IDocumentService iDocumentService) {
        this.iDocumentService = iDocumentService;
    }

    @Override
    public List<ControllerDTO> getControllers(String name, String version) {
        String json;
        try {
            json = iDocumentService.getSwaggerJson(name, version);
        } catch (IOException e) {
            logger.error("fetch swagger json error, service: {}, version: {}, exception: {}", name, version, e.getMessage());
            throw new CommonException("error.service.not.run", name, version);
        }
        return Optional.ofNullable(json)
                .map(j -> processJson2ControllerDTO(j))
                .orElseThrow(() -> new CommonException("error.service.swaggerJson.empty"));
    }

    private List<ControllerDTO> processJson2ControllerDTO(String json) {
        List<ControllerDTO> controllers;
        try {
            JsonNode node = objectMapper.readTree(json);
            controllers = processControllers(node);
            processPaths(node, controllers);
        } catch (IOException e) {
            throw new CommonException("error.parseJson");
        }
        return controllers;
    }

    private void processPaths(JsonNode node, List<ControllerDTO> controllers) {
        JsonNode pathNode = node.get("paths");
        Iterator<String> urlIterator = pathNode.fieldNames();
        while (urlIterator.hasNext()) {
            PathDTO path = new PathDTO();
            String url = urlIterator.next();
            path.setUrl(url);
            JsonNode methodNode = pathNode.get(url);
            Iterator<String> methodIterator = methodNode.fieldNames();
            while (methodIterator.hasNext()) {
                String method = methodIterator.next();
                path.setMethod(method);
                JsonNode jsonNode = methodNode.findValue(method);
                JsonNode tagNode = jsonNode.get("tags");
                for (int i = 0; i < tagNode.size(); i++) {
                    String tag = tagNode.get(i).asText();
                    controllers.forEach(c -> {
                        Set<PathDTO> paths = c.getPaths();
                        if (tag.equals(c.getName())) {
                            paths.add(path);
                        }
                    });
                }
                path.setSummary(Optional.ofNullable(jsonNode.get("summary")).map(n -> n.asText()).orElse(null));
                path.setDescription(Optional.ofNullable(jsonNode.get("description")).map(n -> n.asText()).orElse(null));
                path.setOperationId(Optional.ofNullable(jsonNode.get("operationId")).map(n -> n.asText()).orElse(null));
                path.setOperationId(jsonNode.get("operationId").asText());
                processConsumes(path, jsonNode);
                processProduces(path, jsonNode);
                processParameters(path, jsonNode);
                processResponses(path, jsonNode);
            }
        }
    }

    private void processResponses(PathDTO path, JsonNode jsonNode) {
        JsonNode responseNode = jsonNode.get("responses");
        List<ResponseDTO> responses = new ArrayList<>();
        Iterator<String> responseIterator = responseNode.fieldNames();
        while (responseIterator.hasNext()) {
            String status = responseIterator.next();
            JsonNode node = responseNode.get(status);
            ResponseDTO response = new ResponseDTO();
            response.setHttpStatus(status);
            response.setDescription(node.get("description").asText());
            JsonNode schemaNode = node.get("schema");
            List<String> schemas = new ArrayList<>();
            if (schemaNode != null) {
                for (int i = 0; i < schemaNode.size(); i++) {
                    schemas.add(Optional.ofNullable(schemaNode.get(i)).map(s -> s.asText()).orElse(null));
                }
            }
            response.setSchema(schemas);
            responses.add(response);
        }
        path.setResponses(responses);
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

    private void processParameters(PathDTO path, JsonNode jsonNode) {
        JsonNode parameterNode = jsonNode.get("parameters");
        List<ParameterDTO> parameters = new ArrayList<>();
        if (parameterNode != null) {
            for (int i = 0; i < parameterNode.size(); i++) {
                try {
                    ParameterDTO parameter = objectMapper.treeToValue(parameterNode.get(i), ParameterDTO.class);
                    parameters.add(parameter);
                } catch (JsonProcessingException e) {
                    logger.info("jsonNode to parameterDTO failed, exception: {}", e.getMessage());
                }
            }
        }
        path.setParameters(parameters);
    }

    private List<ControllerDTO> processControllers(JsonNode node) {
        List<ControllerDTO> controllers = new ArrayList<>();
        JsonNode tagNodes = node.get("tags");
        Iterator<JsonNode> iterator = tagNodes.iterator();
        while (iterator.hasNext()) {
            JsonNode jsonNode = iterator.next();
            String name = jsonNode.findValue("name").asText();
            String description = jsonNode.findValue("description").asText();
            ControllerDTO controller = new ControllerDTO();
            controller.setName(name);
            controller.setDescription(description);
            controller.setPaths(new TreeSet<>());
            controllers.add(controller);
        }
        return controllers;
    }
}
