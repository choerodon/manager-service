package io.choerodon.manager.domain.manager.entity;

import java.util.*;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.MultiKeyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.UiConfiguration;

/**
 * @author superleader8@gmail.com
 * @data 2018/3/11
 */
@Component
@Scope("prototype")
public class SwaggerE {
    private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerE.class);

    private Long id;

    private String serviceName;

    private String serviceVersion;

    private Boolean isDefault;

    private String value;

    @Value("${choerodon.swagger.client:client}")
    private String client;

    @Value("${choerodon.swagger.skip.service}")
    private String[] skipService;

    @Autowired
    private RouteE routeE;

    public SecurityConfiguration getSecurityConfiguration() {
        return new SecurityConfiguration(
                client, "unknown", "default",
                "default", "token",
                ApiKeyVehicle.HEADER, "token", ",");
    }

    public UiConfiguration getUiConfiguration() {
        return new UiConfiguration(null);
    }


    public List<SwaggerResource> getSwaggerResource() {
        List<SwaggerResource> resources = new LinkedList<>();
        MultiKeyMap multiKeyMap = routeE.getAllRunningInstances();
        Set set = multiKeyMap.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            MultiKey multiKey = (MultiKey) iterator.next();
            RouteE localRouteE = (RouteE) multiKeyMap.get(multiKey);
            if (localRouteE.getServiceId() != null) {
                boolean isSkipService = Arrays.stream(skipService).anyMatch(t -> t.equals(localRouteE.getServiceId()));
                if (!isSkipService) {
                    SwaggerResource resource = new SwaggerResource();
                    resource.setName(localRouteE.getName() + ":" + localRouteE.getServiceId());
                    resource.setSwaggerVersion("2.0");
                    resource.setLocation("/docs/" + localRouteE.getName() + "?version=" + multiKey.getKey(1));
                    resources.add(resource);
                }
            }
        }
        return resources;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public RouteE getRouteE() {
        return routeE;
    }

    public void setRouteE(RouteE routeE) {
        this.routeE = routeE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
