package io.choerodon.manager.app.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.ServiceConfigDTO;
import io.choerodon.manager.app.service.ServiceConfigService;
import io.choerodon.manager.domain.manager.entity.RouteE;
import io.choerodon.manager.domain.manager.entity.ServiceConfigE;
import io.choerodon.manager.domain.manager.entity.ServiceE;
import io.choerodon.manager.domain.repository.RouteRepository;
import io.choerodon.manager.domain.repository.ServiceConfigRepository;
import io.choerodon.manager.domain.repository.ServiceRepository;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author wuguokai
 */
@Component
public class ServiceConfigServiceImpl implements ServiceConfigService {

    @Value("${choerodon.gateway.names}")
    private String[] getRouteServices;

    private ServiceConfigRepository serviceConfigRepository;
    private ServiceRepository serviceRepository;

    private RouteRepository routeRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceConfigServiceImpl.class);

    public ServiceConfigServiceImpl(ServiceConfigRepository serviceConfigRepository, ServiceRepository serviceRepository, RouteRepository routeRepository) {
        this.serviceConfigRepository = serviceConfigRepository;
        this.serviceRepository = serviceRepository;
        this.routeRepository = routeRepository;
    }


    @Override
    public Page<ServiceConfigDTO> listByServiceId(Long serviceId, PageRequest pageRequest) {
        Page<ServiceConfigDTO> serviceConfigDTOPage = serviceConfigRepository.listByServiceId(serviceId, pageRequest);
        ServiceE serviceE = serviceRepository.getService(serviceId);
        for (String service : getRouteServices) {
            if (service.equals(serviceE.getName())) {
                serviceConfigDTOPage.getContent().forEach(t ->
                        setRoutes(t.getValue())
                );
            }
        }
        return serviceConfigDTOPage;
    }

    @Override
    public Page<ServiceConfigDTO> list(PageRequest pageRequest) {
        Page<ServiceConfigDTO> serviceConfigDTOPage = serviceConfigRepository.list(pageRequest);
        serviceConfigDTOPage.getContent().forEach(t ->
                needSetRoute(t, t.getServiceId())
        );
        return serviceConfigDTOPage;
    }

    @Override
    public ServiceConfigDTO setServiceConfigDefault(Long configId) {
        return ConvertHelper.convert(serviceConfigRepository.setConfigDefault(configId), ServiceConfigDTO.class);
    }

    @Override
    public ServiceConfigDTO query(Long configId) {
        ServiceConfigDTO serviceConfigDTO = ConvertHelper.convert(serviceConfigRepository.query(configId), ServiceConfigDTO.class);
        if (serviceConfigDTO == null) {
            return serviceConfigDTO;
        }
        ServiceE serviceE = serviceRepository.getService(serviceConfigDTO.getServiceId());
        if (serviceE == null) {
            throw new CommonException("error.service.notExist", serviceConfigDTO.getServiceId());
        }
        for (String service : getRouteServices) {
            if (service.equals(serviceE.getName())) {
                setRoutes(serviceConfigDTO.getValue());
            }
        }
        return serviceConfigDTO;
    }

    @Override
    public Boolean delete(Long configId) {
        return serviceConfigRepository.delete(configId);
    }

    @Override
    public ServiceConfigDTO update(Long configId, ServiceConfigDTO serviceConfigDTO) {
        return ConvertHelper.convert(serviceConfigRepository.update(configId, ConvertHelper.convert(serviceConfigDTO, ServiceConfigE.class)), ServiceConfigDTO.class);
    }

    //循环判断配置是否需要添加route信息
    public void needSetRoute(ServiceConfigDTO serviceConfigDTO, Long serviceId) {
        ServiceE serviceE = serviceRepository.getService(serviceId);
        if (serviceE == null) {
            throw new CommonException("error.service.notExist", serviceId);
        }
        for (String service : getRouteServices) {
            if (service.equals(serviceE.getName())) {
                setRoutes(serviceConfigDTO.getValue());
            }
        }
    }

    /**
     * 提取出来的判断方法
     *
     * @param map 要添加路由信息的map
     */
    public void setRoutes(final Map map) {
        List<RouteE> routeEList = routeRepository.getAllRoute();
        for (RouteE routeE : routeEList) {
            String prefix = "zuul.routes." + routeE.getName() + ".";
            if (routeE.getPath() != null) {
                map.put(prefix + "path", routeE.getPath());
            }
            if (routeE.getServiceId() != null) {
                map.put(prefix + "serviceId", routeE.getServiceId());
            }
            if (routeE.getUrl() != null) {
                map.put(prefix + "url", routeE.getUrl());
            }
            if (routeE.getStripPrefix() != null) {
                map.put(prefix + "stripPrefix", routeE.getStripPrefix());
            }
            if (routeE.getRetryable() != null) {
                map.put(prefix + "retryable", routeE.getRetryable());
            }
            if (routeE.getHelperService() != null) {
                map.put(prefix + "helperService", routeE.getHelperService());
            }
            if (routeE.getCustomSensitiveHeaders() != null && routeE.getCustomSensitiveHeaders()) {
                map.put(prefix + "customSensitiveHeaders", routeE.getCustomSensitiveHeaders());
            }
            if (routeE.getSensitiveHeaders() != null) {
                map.put(prefix + "sensitiveHeaders", routeE.getSensitiveHeaders());
            }
        }
    }


    @Override
    public ServiceConfigDTO queryDefaultByServiceName(String serviceName) {
        ServiceConfigDTO serviceConfigDTO = serviceConfigRepository.queryDefaultByServiceName(serviceName);
        if (serviceConfigDTO == null) {
            LOGGER.info("$${}$$", serviceName);
            throw new CommonException("error.serviceConfigDO.query.serviceNameNotFound");
        }
        for (String service : getRouteServices) {
            if (service.equals(serviceName)) {
                setRoutes(serviceConfigDTO.getValue());
            }
        }
        return serviceConfigDTO;
    }


    @Override
    public ServiceConfigDTO queryByServiceNameAndConfigVersion(String serviceName, String configVersion) {
        ServiceConfigDTO serviceConfigDTO =
                serviceConfigRepository.queryByServiceNameAndConfigVersion(serviceName, configVersion);
        if (serviceConfigDTO == null) {
            throw new CommonException("error.serviceConfigDO.query.serviceNameOrConfigVersionNotFound");
        }
        for (String service : getRouteServices) {
            if (service.equals(serviceName)) {
                setRoutes(serviceConfigDTO.getValue());
            }
        }
        return serviceConfigDTO;
    }

}
