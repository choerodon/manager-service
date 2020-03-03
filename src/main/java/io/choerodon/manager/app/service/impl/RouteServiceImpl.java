package io.choerodon.manager.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.app.service.RouteService;
import io.choerodon.manager.infra.common.utils.VersionUtil;
import io.choerodon.manager.infra.dto.RouteDTO;
import io.choerodon.manager.infra.feign.IamClient;
import org.apache.commons.collections.map.MultiKeyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Iterator;
import java.util.List;

/**
 * 应用层实现
 *
 * @author wuguokai
 */
@Component
public class RouteServiceImpl implements RouteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteServiceImpl.class);

    private DiscoveryClient discoveryClient;
    private IamClient iamClient;

    private String registerUrl;
    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String ADD_ZUUL_ROOT_URL = "/zuul";
    private static final String DELETE_ZUUL_ROOT_URL = "/zuul/delete";

    /**
     * 构造器
     */
    public RouteServiceImpl(@Value("${eureka.client.serviceUrl.defaultZone}") String registerUrl,
                            DiscoveryClient discoveryClient, IamClient iamClient) {
        this.registerUrl = registerUrl;
        this.discoveryClient = discoveryClient;
        this.iamClient = iamClient;
    }


    private void modifyRouteFromGoRegister(RouteDTO routeDTO, String suffix, String message) {
        String zuulRootUrl = getZuulRootUrl(suffix);
        try {
            restTemplate.postForEntity(zuulRootUrl, routeDTO, Void.class);
        } catch (Exception e) {
            throw new CommonException(message);
        }
    }

    private String getZuulRootUrl(String suffix) {
        if (!registerUrl.endsWith("/eureka") && !registerUrl.endsWith("/eureka/")) {
            throw new CommonException("error.illegal.register-service.url");
        }
        String[] array = registerUrl.split("/eureka");
        String registerHost = array[0];
        return registerHost + suffix;
    }

    @Override
    public MultiKeyMap getAllRunningInstances() {
        List<RouteDTO> routes = iamClient.selectRoute(null);
        LOGGER.info("routes is {}", routes.toString());
        List<String> serviceIds = discoveryClient.getServices();
        LOGGER.info("serviceIds is {}", serviceIds.toString());
        MultiKeyMap multiKeyMap = new MultiKeyMap();
        for (String serviceIdInList : serviceIds) {
            for (ServiceInstance instance : discoveryClient.getInstances(serviceIdInList)) {
                LOGGER.info("instance is {}", instance);
                String version = instance.getMetadata().get(VersionUtil.METADATA_VERSION);
                if (org.springframework.util.StringUtils.isEmpty(version)) {
                    version = VersionUtil.NULL_VERSION;
                }
                if (multiKeyMap.get(serviceIdInList, version) == null) {
                    RouteDTO routeDTO = selectZuulRouteByServiceId(routes, serviceIdInList);
                    if (routeDTO == null) {
                        continue;
                    }
                    multiKeyMap.put(serviceIdInList, version, routeDTO);
                }
            }
        }
        return multiKeyMap;
    }

    private RouteDTO selectZuulRouteByServiceId(List<RouteDTO> routes, String serviceId) {
        for (RouteDTO routeDTO : routes) {
            if (routeDTO.getServiceCode().equals(serviceId)) {
                return routeDTO;
            }
        }
        return null;
    }

    @Override
    public RouteDTO getRouteFromRunningInstancesMap(MultiKeyMap runningMap, String name, String version) {
        Iterator iterator = runningMap.values().iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            if (object instanceof RouteDTO) {
                RouteDTO routeDTO = (RouteDTO) object;
                if (String.format("/%s/**", name).equals(routeDTO.getBackendPath())) {
                    return routeDTO;
                }
            }
        }
        return null;
    }
}
