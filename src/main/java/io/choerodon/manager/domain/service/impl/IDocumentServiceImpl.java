package io.choerodon.manager.domain.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.choerodon.core.exception.CommonException;
import io.choerodon.eureka.event.EurekaEventPayload;
import io.choerodon.manager.domain.manager.entity.RouteE;
import io.choerodon.manager.domain.service.IDocumentService;
import io.choerodon.manager.domain.service.IRouteService;
import io.choerodon.manager.infra.common.utils.VersionUtil;
import io.choerodon.manager.infra.dataobject.SwaggerDO;
import io.choerodon.manager.infra.mapper.SwaggerMapper;
import io.swagger.models.auth.OAuth2Definition;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

/**
 * {@inheritDoc}
 *
 * @author xausky
 * @author wuguokai
 */
@org.springframework.stereotype.Service
public class IDocumentServiceImpl implements IDocumentService {


    @Value("${choerodon.profiles.active:sit}")
    private String profiles;
    private static final Logger LOGGER = LoggerFactory.getLogger(IDocumentServiceImpl.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String METADATA_CONTEXT = "CONTEXT";
    private static final String DEFAULT = "default";
    @Value("${choerodon.swagger.oauth-url:http://localhost:8080/iam/oauth/authorize}")
    private String oauthUrl;
    @Value("${choerodon.swagger.client:client}")
    private String client;
    @Value("${choerodon.swagger.local.enabled:false}")
    private Boolean swaggerLocal;
    @Value("${choerodon.gateway.domain:localhost}")
    private String gatewayDomain;
    private RestTemplate restTemplate = new RestTemplate();
    private SwaggerMapper swaggerMapper;
    private DiscoveryClient discoveryClient;
    private IRouteService iRouteService;

    /**
     * 构造器
     */
    public IDocumentServiceImpl(SwaggerMapper swaggerMapper,
                                DiscoveryClient discoveryClient, IRouteService iRouteService) {
        this.swaggerMapper = swaggerMapper;
        this.discoveryClient = discoveryClient;
        this.iRouteService = iRouteService;
    }

    public void setProfiles(String profiles) {
        this.profiles = profiles;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void setOauthUrl(String oauthUrl) {
        this.oauthUrl = oauthUrl;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ObjectNode buildSwaggerJson(String json) throws IOException {
        if (StringUtils.isEmpty(json)) {
            throw new RemoteAccessException("fetch swagger json failed");
        }
        ObjectNode node = (ObjectNode) MAPPER.readTree(json);
        List<Map<String, List<String>>> security = new LinkedList<>();
        Map<String, List<String>> clients = new TreeMap<>();
        clients.put(client, Collections.singletonList(DEFAULT));
        security.add(clients);
        OAuth2Definition definition = new OAuth2Definition();
        definition.setAuthorizationUrl(oauthUrl);
        definition.setType("oauth2");
        definition.setFlow("implicit");
        definition.setScopes(Collections.singletonMap(DEFAULT, "default scope"));
        LOGGER.info("{}", definition.getScopes());
        node.putPOJO("securityDefinitions", Collections.singletonMap(client, definition));
        Iterator<Map.Entry<String, JsonNode>> pathIterator = node.get("paths").fields();
        while (pathIterator.hasNext()) {
            Map.Entry<String, JsonNode> pathNode = pathIterator.next();
            Iterator<Map.Entry<String, JsonNode>> methodIterator = pathNode.getValue().fields();
            while (methodIterator.hasNext()) {
                Map.Entry<String, JsonNode> methodNode = methodIterator.next();
                ((ObjectNode) methodNode.getValue()).putPOJO("security", security);
            }
        }
        return node;
    }

    @Override
    public String fetchSwaggerJsonByService(String service, String version) {
        SwaggerDO query = new SwaggerDO();
        query.setServiceName(service);
        query.setServiceVersion(version);
        SwaggerDO data = swaggerMapper.selectOne(query);
        if (profiles.equals(DEFAULT) || data == null || StringUtils.isEmpty(data.getValue())) {
            String json = fetchFromDiscoveryClient(service, version);
            if (json != null && data == null) {
                //insert
                SwaggerDO insertSwagger = new SwaggerDO();
                insertSwagger.setServiceName(service);
                insertSwagger.setServiceVersion(version);
                insertSwagger.setDefault(false);
                insertSwagger.setValue(json);
                if (swaggerMapper.insertSelective(insertSwagger) != 1) {
                    LOGGER.warn("insert swagger error, swagger : {}", insertSwagger);
                }
            } else if (json != null && StringUtils.isEmpty(data.getValue())) {
                //update
                query.setValue(json);
                if (swaggerMapper.updateByPrimaryKeySelective(query) != 1) {
                    LOGGER.warn("update swagger error, swagger : {}", query);
                }
            }
            return json;
        } else {
            return data.getValue();
        }
    }

    private String fetchFromDiscoveryClient(String service, String version) {
        List<ServiceInstance> instances = discoveryClient.getInstances(service);
        List<String> mdVersions = new ArrayList<>();
        for (ServiceInstance instance : instances) {
            String mdVersion = instance.getMetadata().get(VersionUtil.METADATA_VERSION);
            mdVersions.add(mdVersion);
            if (StringUtils.isEmpty(mdVersion)) {
                mdVersion = VersionUtil.NULL_VERSION;
            }
            if (version.equals(mdVersion)) {
                return fetch(instance);
            }
        }
        LOGGER.warn("service {} running instances {} do not contain the version {} ", service, mdVersions, version);
        return null;
    }

    @Override
    public String expandSwaggerJson(String name, String version, String json) throws IOException {
        MultiKeyMap multiKeyMap = iRouteService.getAllRunningInstances();
        RouteE routeE = iRouteService
                .getRouteFromRunningInstancesMap(multiKeyMap, name, version);
        if (routeE == null) {
            return "";
        }
        String basePath = routeE.getPath().replace("/**", "");
        if (swaggerLocal) {
            basePath = "/";
            gatewayDomain = "localhost:8963";
        }
        ObjectNode root = buildSwaggerJson(json);
        root.put("basePath", basePath);
        root.put("host", gatewayDomain);
        LOGGER.debug("put basePath:{}, host:{}", basePath, root.get("host"));
        return MAPPER.writeValueAsString(root);
    }

    private String fetch(ServiceInstance instance) {
        ResponseEntity<String> response;
        String contextPath = instance.getMetadata().get(METADATA_CONTEXT);
        if (contextPath == null) {
            contextPath = "";
        }
        LOGGER.info("service: {} metadata : {}", instance.getServiceId(), instance.getMetadata());
        try {
            response = restTemplate.getForEntity(
                    instance.getUri() + contextPath + "/v2/choerodon/api-docs",
                    String.class);
        } catch (RestClientException e) {
            String msg = "fetch failed, instance:" + instance.getServiceId() + ", uri: " + instance.getUri() + ", contextPath: " + contextPath;
            throw new RemoteAccessException(msg);
        }
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RemoteAccessException("fetch failed : " + response);
        }
        return response.getBody();
    }

    @Override
    public String fetchSwaggerJsonByIp(final EurekaEventPayload payload) {
        ResponseEntity<String> response = restTemplate.getForEntity("http://" + payload.getInstanceAddress() + "/v2/choerodon/api-docs",
                String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new CommonException("fetch swagger error, statusCode is not 2XX, serviceId: " + payload.getId());
        }

    }

}
