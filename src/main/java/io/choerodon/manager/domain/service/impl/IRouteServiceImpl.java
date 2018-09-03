package io.choerodon.manager.domain.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.manager.api.dto.RouteDTO;
import io.choerodon.manager.domain.manager.entity.RouteE;
import io.choerodon.manager.domain.repository.RouteRepository;
import io.choerodon.manager.domain.service.IRouteService;
import io.choerodon.manager.infra.common.utils.VersionUtil;
import io.choerodon.manager.infra.dataobject.RouteDO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.swagger.extra.ExtraData;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 实现类
 *
 * @author wuguokai
 */
@Service
public class IRouteServiceImpl implements IRouteService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IRouteServiceImpl.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String METADATA_CONTEXT = "CONTEXT";
    private final ObjectMapper mapper = new ObjectMapper();
    private RestTemplate restTemplate = new RestTemplate();
    private RouteRepository routeRepository;
    private DiscoveryClient discoveryClient;

    public IRouteServiceImpl(RouteRepository routeRepository, DiscoveryClient discoveryClient) {
        this.routeRepository = routeRepository;
        this.discoveryClient = discoveryClient;
    }

    @Override
    public Page<RouteE> pageAll(PageRequest pageRequest, RouteDO routeDO, String params) {
        return routeRepository.pageAllRoutes(pageRequest, routeDO, params);
    }

    @Override
    @Transactional
    public List<RouteE> addRoutes(List<RouteE> routeEList) {
        return routeRepository.addRoutesBatch(routeEList);
    }

    @Override
    public List<RouteE> getAll() {
        return routeRepository.getAllRoute();
    }

    @Override
    public MultiKeyMap getAllRunningInstances() {
        List<RouteE> routeEList = routeRepository.getAllRoute();
        List<String> serviceIds = discoveryClient.getServices();
        MultiKeyMap multiKeyMap = new MultiKeyMap();
        for (String serviceIdInList : serviceIds) {
            for (ServiceInstance instance : discoveryClient.getInstances(serviceIdInList)) {
                String version = instance.getMetadata().get(VersionUtil.METADATA_VERSION);
                if (org.springframework.util.StringUtils.isEmpty(version)) {
                    version = VersionUtil.NULL_VERSION;
                }
                if (multiKeyMap.get(serviceIdInList, version) == null) {
                    RouteE routeE = selectZuulRouteByServiceId(routeEList, serviceIdInList);
                    if (routeE == null) {
                        continue;
                    }
                    multiKeyMap.put(serviceIdInList, version, routeE);
                }
            }
        }
        return multiKeyMap;
    }

    private RouteE selectZuulRouteByServiceId(List<RouteE> routeEList, String serviceId) {
        for (RouteE routeE : routeEList) {
            if (routeE.getServiceId().equals(serviceId)) {
                return routeE;
            }
        }
        return null;
    }

    @Override
    public RouteE getRouteFromRunningInstancesMap(MultiKeyMap runningMap, String name, String version) {
        Iterator iterator = runningMap.values().iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            if (object instanceof RouteE) {
                RouteE routeE = (RouteE) object;
                if (name.equals(routeE.getName())) {
                    return routeE;
                }
            }
        }
        return null;
    }

    @Override
    @Transactional
    public void autoRefreshRoute(String swaggerJson) {
        ExtraData extraData;
        Map swaggerMap = null;
        ChoerodonRouteData data;
        try {
            swaggerMap = objectMapper.readValue(swaggerJson, Map.class);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        if (swaggerMap != null) {
            Object object = swaggerMap.get(ExtraData.EXTRA_DATA_KEY);
            if (object != null) {
                extraData = objectMapper.convertValue(object, ExtraData.class);
                if (extraData != null) {
                    data = objectMapper.convertValue(extraData.getData().get(ExtraData.ZUUL_ROUTE_DATA), ChoerodonRouteData.class);
                    if (data != null) {
                        executeRefreshRoute(data);
                    }
                }
            }
        }
    }

    private void executeRefreshRoute(final ChoerodonRouteData data) {
        RouteDO routeDO = new RouteDO();
        setRoute(data, routeDO);
        RouteE routeE = routeRepository.queryRoute(ConvertHelper.convert(new RouteDO(data.getName(), data.getPath()), RouteE.class));
        if (routeE == null) {
            routeRepository.addRoute(ConvertHelper.convert(routeDO, RouteE.class));
            LOGGER.info("{} : 初始化路由成功", routeDO.getName());
        } else {
            routeDO.setObjectVersionNumber(routeE.getObjectVersionNumber());
            routeDO.setId(routeE.getId());
            routeRepository.updateRoute(ConvertHelper.convert(routeDO, RouteE.class));
            LOGGER.info("{} : rout update success", routeDO.getName());
        }
    }

    private void setRoute(ChoerodonRouteData choerodonRouteData, RouteDO routeDO) {
        routeDO.setName(choerodonRouteData.getName());
        routeDO.setPath(choerodonRouteData.getPath());
        routeDO.setServiceId(choerodonRouteData.getServiceId());
        routeDO.setRetryable(choerodonRouteData.getRetryable());
        routeDO.setCustomSensitiveHeaders(choerodonRouteData.getCustomSensitiveHeaders());
        routeDO.setHelperService(choerodonRouteData.getHelperService());
        routeDO.setSensitiveHeaders(choerodonRouteData.getSensitiveHeaders());
        routeDO.setStripPrefix(choerodonRouteData.getStripPrefix());
        routeDO.setUrl(choerodonRouteData.getUrl());
    }

    @Override
    public ChoerodonRouteData fetchRouteData(String service, String version) {
        List<ServiceInstance> instances = discoveryClient.getInstances(service);
        for (ServiceInstance instance : instances) {
            String mdVersion = instance.getMetadata().get(VersionUtil.METADATA_VERSION);
            if (StringUtils.isEmpty(mdVersion)) {
                mdVersion = VersionUtil.NULL_VERSION;
            }
            if (version.equals(mdVersion)) {
                return fetch(instance);
            }
        }
        return null;
    }

    private ChoerodonRouteData fetch(ServiceInstance instance) {
        ResponseEntity<String> response;
        String contextPath = instance.getMetadata().get(METADATA_CONTEXT);
        if (contextPath == null) {
            contextPath = "";
        }
        if ("OAUTH-SERVER".equals(instance.getServiceId())) {
            contextPath = "/oauth";
        }
        try {
            response = restTemplate.getForEntity(
                    instance.getUri() + contextPath + "/v2/route_json",
                    String.class);
        } catch (RestClientException e) {
            throw new RemoteAccessException("fetch failed, instance:" + instance.getServiceId());
        }
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RemoteAccessException("fetch failed : " + response);
        }
        try {
            return mapper.readValue(response.getBody(), ChoerodonRouteData.class);
        } catch (IOException e) {
            LOGGER.info("fetch ChoerodonRouteData error");
            return null;
        }
    }

    @Override
    public RouteE queryRouteByService(String service) {
        RouteDTO routeDTO = new RouteDTO();
        routeDTO.setServiceId(service);
        return routeRepository.queryRoute(ConvertHelper.convert(routeDTO, RouteE.class));
    }
}
