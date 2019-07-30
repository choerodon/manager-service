package io.choerodon.manager.app.service.impl;

import java.util.*;

import io.choerodon.eureka.event.EurekaEventProperties;
import io.choerodon.manager.app.service.RouteService;
import io.choerodon.manager.infra.dto.RouteDTO;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.MultiKeyMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.UiConfiguration;

import io.choerodon.manager.app.service.SwaggerService;

/**
 * @author superleader8@gmail.com
 * @data 2018/3/15
 */
@Component
public class SwaggerServiceImpl implements SwaggerService {

    private EurekaEventProperties properties;

    private String client;

    private RouteService routeService;

    private final AntPathMatcher matcher = new AntPathMatcher();

    public SwaggerServiceImpl(@Value("${choerodon.swagger.client:client}") String client,
                              EurekaEventProperties properties,
                              RouteService routeService) {
        this.client = client;
        this.properties = properties;
        this.routeService = routeService;
    }

    @Override
    public List<SwaggerResource> getSwaggerResource() {
        List<SwaggerResource> swaggerResources = processSwaggerResource();
        swaggerResources.sort(Comparator.comparing(SwaggerResource::getName));
        return swaggerResources;
    }

    private List<SwaggerResource> processSwaggerResource() {
        List<SwaggerResource> resources = new LinkedList<>();
        //key1:服务名 key2:版本 value:route
        MultiKeyMap multiKeyMap = routeService.getAllRunningInstances();
        Set set = multiKeyMap.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            MultiKey multiKey = (MultiKey) iterator.next();
            RouteDTO route = (RouteDTO) multiKeyMap.get(multiKey);
            String serviceId = route.getServiceId();
            if (serviceId != null) {
                boolean isSkip =
                        Arrays.stream(properties.getSkipServices()).anyMatch(t -> matcher.match(t, serviceId));
                if (!isSkip) {
                    SwaggerResource resource = new SwaggerResource();
                    resource.setName(route.getName() + ":" + serviceId);
                    resource.setSwaggerVersion("2.0");
                    resource.setLocation("/docs/" + route.getName() + "?version=" + multiKey.getKey(1));
                    resources.add(resource);
                }
            }
        }
        return resources;
    }

    @Override
    public UiConfiguration getUiConfiguration() {
        return new UiConfiguration(null);
    }

    @Override
    public SecurityConfiguration getSecurityConfiguration() {

        return new SecurityConfiguration(client, "unknown",
                "default", "default", "token",
                ApiKeyVehicle.HEADER, "token", ",");
    }
}
