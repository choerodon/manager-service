package io.choerodon.manager.app.service.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.ConfigDTO;
import io.choerodon.manager.app.service.ConfigService;
import io.choerodon.manager.domain.manager.entity.ConfigE;
import io.choerodon.manager.domain.manager.entity.RouteE;
import io.choerodon.manager.domain.manager.entity.ServiceE;
import io.choerodon.manager.domain.repository.ConfigRepository;
import io.choerodon.manager.domain.repository.RouteRepository;
import io.choerodon.manager.domain.repository.ServiceRepository;
import io.choerodon.manager.infra.dataobject.ConfigDO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author wuguokai
 */
@Component
public class ConfigServiceImpl implements ConfigService {

    @Value("${choerodon.gateway.names}")
    private String[] getRouteServices;

    private ConfigRepository configRepository;
    private ServiceRepository serviceRepository;

    private RouteRepository routeRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigServiceImpl.class);

    public ConfigServiceImpl(ConfigRepository configRepository, ServiceRepository serviceRepository, RouteRepository routeRepository) {
        this.configRepository = configRepository;
        this.serviceRepository = serviceRepository;
        this.routeRepository = routeRepository;
    }


    @Override
    public Page<ConfigDTO> listByServiceId(Long serviceId, PageRequest pageRequest) {
        Page<ConfigDTO> configDTOPage = configRepository.listByServiceId(serviceId, pageRequest);
        ServiceE serviceE = serviceRepository.getService(serviceId);
        if (ArrayUtils.contains(getRouteServices, serviceE.getName())) {
            final List<RouteE> routeEList = routeRepository.getAllRoute();
            configDTOPage.getContent().forEach(t ->
                    setRoutes(routeEList, t.getValue())
            );
        }
        return configDTOPage;
    }

    @Override
    public Page<ConfigDTO> list(PageRequest pageRequest) {
        Page<ConfigDTO> configDTOPage = configRepository.list(pageRequest);
        configDTOPage.getContent().forEach(t ->
                needSetRoute(t, t.getServiceId())
        );
        return configDTOPage;
    }

    @Override
    public ConfigDTO setServiceConfigDefault(Long configId) {
        return ConvertHelper.convert(configRepository.setConfigDefault(configId), ConfigDTO.class);
    }

    @Override
    public ConfigDTO query(Long configId) {
        ConfigDTO configDTO = ConvertHelper.convert(configRepository.query(configId), ConfigDTO.class);
        if (configDTO == null) {
            return null;
        }
        ServiceE serviceE = serviceRepository.getService(configDTO.getServiceId());
        if (serviceE == null) {
            throw new CommonException("error.service.notExist", configDTO.getServiceId());
        }
        if (ArrayUtils.contains(getRouteServices, serviceE.getName())) {
            final List<RouteE> routeEList = routeRepository.getAllRoute();
            setRoutes(routeEList, configDTO.getValue());
        }
        return configDTO;
    }

    @Override
    public Boolean delete(Long configId) {
        return configRepository.delete(configId);
    }

    @Override
    public ConfigDTO update(Long configId, ConfigDTO configDTO) {
        return ConvertHelper.convert(configRepository.update(configId, ConvertHelper.convert(configDTO, ConfigE.class)), ConfigDTO.class);
    }

    //循环判断配置是否需要添加route信息
    private void needSetRoute(ConfigDTO configDTO, Long serviceId) {
        ServiceE serviceE = serviceRepository.getService(serviceId);
        if (serviceE == null) {
            throw new CommonException("error.service.notExist", serviceId);
        }
        if (ArrayUtils.contains(getRouteServices, serviceE.getName())) {
            final List<RouteE> routeEList = routeRepository.getAllRoute();
            setRoutes(routeEList, configDTO.getValue());
        }
    }

    /**
     * 提取出来的判断方法
     */
    @SuppressWarnings("unchecked")
    private void setRoutes(final List<RouteE> routeEList, final Map map) {
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
    public ConfigDTO queryDefaultByServiceName(String serviceName) {
        ConfigDTO configDTO = configRepository.queryDefaultByServiceName(serviceName);
        if (configDTO == null) {
            LOGGER.info("$${}$$", serviceName);
            throw new CommonException("error.serviceConfigDO.query.serviceNameNotFound");
        }
        if (ArrayUtils.contains(getRouteServices, serviceName)) {
            final List<RouteE> routeEList = routeRepository.getAllRoute();
            setRoutes(routeEList, configDTO.getValue());
        }
        return configDTO;
    }


    @Override
    public ConfigDTO queryByServiceNameAndConfigVersion(String serviceName, String configVersion) {
        ConfigDTO configDTO =
                configRepository.queryByServiceNameAndConfigVersion(serviceName, configVersion);
        if (configDTO == null) {
            throw new CommonException("error.serviceConfigDO.query.serviceNameOrConfigVersionNotFound");
        }
        if (ArrayUtils.contains(getRouteServices, serviceName)) {
            final List<RouteE> routeEList = routeRepository.getAllRoute();
            setRoutes(routeEList, configDTO.getValue());
        }
        return configDTO;
    }

    @Override
    public List<ConfigDTO> listByServiceName(String serviceName) {
        List<ConfigDTO> configDTOList = configRepository.listByServiceName(serviceName);
        if (ArrayUtils.contains(getRouteServices, serviceName)) {
            final List<RouteE> routeEList = routeRepository.getAllRoute();
            configDTOList.forEach(t -> setRoutes(routeEList, t.getValue()));
        }
        return configDTOList;
    }

    @Override
    public ConfigDTO create(ConfigDTO configDTO) {
        return ConvertHelper.convert(configRepository.create(ConvertHelper
                .convert(configDTO, ConfigDO.class)), ConfigDTO.class);
    }
}
