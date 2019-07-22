package io.choerodon.manager.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.InsertException;
import io.choerodon.core.exception.ext.UpdateExcetion;
import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.manager.app.service.RouteService;
import io.choerodon.manager.domain.manager.entity.RouteE;
import io.choerodon.manager.domain.repository.RouteRepository;
import io.choerodon.manager.infra.asserts.RouteAssertHelper;
import io.choerodon.manager.infra.common.utils.VersionUtil;
import io.choerodon.manager.infra.dto.RouteDTO;
import io.choerodon.manager.infra.mapper.RouteMapper;
import io.choerodon.swagger.swagger.extra.ExtraData;
import org.apache.commons.collections.map.MultiKeyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 应用层实现
 *
 * @author wuguokai
 */
@Component
public class RouteServiceImpl implements RouteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteServiceImpl.class);

    private RouteRepository routeRepository;

    private DiscoveryClient discoveryClient;

    private RouteMapper routeMapper;

    private RouteAssertHelper routeAssertHelper;

    private String registerUrl;
    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String ADD_ZUUL_ROOT_URL = "/zuul";
    private static final String DELETE_ZUUL_ROOT_URL = "/zuul/delete";

    /**
     * 构造器
     */
    public RouteServiceImpl(@Value("${eureka.client.serviceUrl.defaultZone}") String registerUrl,
                            RouteRepository routeRepository,
                            DiscoveryClient discoveryClient,
                            RouteMapper routeMapper,
                            RouteAssertHelper routeAssertHelper) {
        this.registerUrl = registerUrl;
        this.routeRepository = routeRepository;
        this.discoveryClient = discoveryClient;
        this.routeMapper = routeMapper;
        this.routeAssertHelper = routeAssertHelper;
    }

    @Override
    public PageInfo<RouteDTO> list(PageRequest pageRequest, RouteDTO routeDTO, String params) {
        return PageHelper
                .startPage(pageRequest.getPage(), pageRequest.getSize())
                .doSelectPageInfo(() -> routeMapper.selectRoutes(routeDTO, params));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RouteDTO create(RouteDTO routeDTO) {
        //创建路由为非内置的
        routeDTO.setId(null);
        routeDTO.setBuiltIn(false);
        String name = routeDTO.getName();
        String path = routeDTO.getPath();
        routeAssertHelper.nameExisted(name);
        routeAssertHelper.pathExisted(path);

        if (routeMapper.insert(routeDTO) != 1) {
            throw new InsertException("error.route.insert");
        }
        //调用go-register接口，插入到config map里
        modifyRouteFromGoRegister(routeDTO, ADD_ZUUL_ROOT_URL, "error to add route to register server");
        return routeMapper.selectByPrimaryKey(routeDTO);
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
    public RouteDTO update(Long id, RouteDTO routeDTO) {
        routeDTO.setId(id);
        routeAssertHelper.objectVersionNumberNotNull(routeDTO.getObjectVersionNumber());
        RouteDTO route = routeAssertHelper.notExisted(id);
        if (route.getBuiltIn()) {
            throw new CommonException("error.route.updateBuiltIn");
        }
        route.setBuiltIn(null);
        if (routeMapper.updateByPrimaryKeySelective(routeDTO) != 1) {
            throw new UpdateExcetion("error.route.update");
        }
        modifyRouteFromGoRegister(routeDTO, ADD_ZUUL_ROOT_URL, "error to update route to register server");
        return routeMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long routeId) {
        routeRepository.delete(routeId);
    }

    @Override
    public void checkRoute(RouteDTO routeDTO) {
        String name = routeDTO.getName();
        String path = routeDTO.getPath();
        if (!StringUtils.isEmpty(name)) {
            routeAssertHelper.nameExisted(name);
        }
        if (!StringUtils.isEmpty(path)) {
            routeAssertHelper.pathExisted(path);
        }
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
        try {
            Map swaggerMap = objectMapper.readValue(swaggerJson, Map.class);
            if (swaggerMap != null) {
                Object object = swaggerMap.get(ExtraData.EXTRA_DATA_KEY);
                if (object != null) {
                    ExtraData extraData = objectMapper.convertValue(object, ExtraData.class);
                    if (extraData != null) {
                        ChoerodonRouteData data = objectMapper.convertValue(extraData.getData().get(ExtraData.ZUUL_ROUTE_DATA), ChoerodonRouteData.class);
                        if (data != null) {
                            executeRefreshRoute(data);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new CommonException("error.refreshRoute.IOException", e);
        }
    }

    private void executeRefreshRoute(final ChoerodonRouteData data) {
        RouteDTO routeDTO = new RouteDTO();
        setRoute(data, routeDTO);
        RouteE routeE = routeRepository.queryRoute(ConvertHelper.convert(new RouteDTO(data.getName(), data.getPath()), RouteE.class));
        if (routeE == null) {
            routeRepository.addRoute(ConvertHelper.convert(routeDTO, RouteE.class));
            LOGGER.info("{} : 初始化路由成功", routeDTO.getName());
        } else {
            routeDTO.setObjectVersionNumber(routeE.getObjectVersionNumber());
            routeDTO.setId(routeE.getId());
            routeRepository.updateRoute(ConvertHelper.convert(routeDTO, RouteE.class));
            LOGGER.info("{} : rout update success", routeDTO.getName());
        }
    }

    private void setRoute(ChoerodonRouteData choerodonRouteData, RouteDTO routeDTO) {
        routeDTO.setName(choerodonRouteData.getName());
        routeDTO.setPath(choerodonRouteData.getPath());
        routeDTO.setServiceId(choerodonRouteData.getServiceId());
        routeDTO.setRetryable(choerodonRouteData.getRetryable());
        routeDTO.setCustomSensitiveHeaders(choerodonRouteData.getCustomSensitiveHeaders());
        routeDTO.setHelperService(choerodonRouteData.getHelperService());
        routeDTO.setSensitiveHeaders(choerodonRouteData.getSensitiveHeaders());
        routeDTO.setStripPrefix(choerodonRouteData.getStripPrefix());
        routeDTO.setUrl(choerodonRouteData.getUrl());
    }
}
