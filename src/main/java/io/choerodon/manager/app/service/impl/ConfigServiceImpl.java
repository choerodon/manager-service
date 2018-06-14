package io.choerodon.manager.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.ConfigDTO;
import io.choerodon.manager.api.dto.CreateConfigDTO;
import io.choerodon.manager.api.dto.ItemDto;
import io.choerodon.manager.api.dto.YamlDto;
import io.choerodon.manager.app.service.ConfigService;
import io.choerodon.manager.domain.manager.entity.RouteE;
import io.choerodon.manager.domain.manager.entity.ServiceE;
import io.choerodon.manager.domain.repository.ConfigRepository;
import io.choerodon.manager.domain.repository.RouteRepository;
import io.choerodon.manager.domain.repository.ServiceRepository;
import io.choerodon.manager.infra.common.annotation.ConfigNotifyRefresh;
import io.choerodon.manager.infra.common.utils.config.ConfigUtil;
import io.choerodon.manager.infra.dataobject.ConfigDO;
import io.choerodon.manager.infra.dataobject.ServiceDO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.util.StringUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author wuguokai
 */
@Component
public class ConfigServiceImpl implements ConfigService {

    private final ObjectMapper mapper = new ObjectMapper();

    public static final String CONFIG_TYPE_PROPERTIES = "properties";

    public static final String CONFIG_TYPE_YAML = "yaml";

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
    public Page<ConfigDTO> listByServiceName(String serviceName, PageRequest pageRequest, ConfigDTO queryInfo, String queryParam) {
        return configRepository.listByServiceName(serviceName, pageRequest,
                ConvertHelper.convert(queryInfo, ConfigDO.class), queryParam);
    }

    @Override
    public ConfigDTO setServiceConfigDefault(Long configId) {
        return ConvertHelper.convert(configRepository.setConfigDefault(configId), ConfigDTO.class);
    }

    @Override
    public ConfigDTO query(Long configId, String type) {
        ConfigDTO configDTO = ConvertHelper.convert(configRepository.query(configId), ConfigDTO.class);
        if (configDTO == null) {
            throw new CommonException("error.config.not.exist");
        }
        ServiceE serviceE = serviceRepository.getService(configDTO.getServiceId());
        if (serviceE == null) {
            throw new CommonException("error.service.notExist", configDTO.getServiceId());
        }
        if (ArrayUtils.contains(getRouteServices, serviceE.getName())) {
            final List<RouteE> routeEList = routeRepository.getAllRoute();
            setRoutes(routeEList, configDTO.getValue());
        }
        if (!StringUtils.isEmpty(type)) {
            configDTO.setTxt(ConfigUtil.convertMapToText(configDTO.getValue(), type));
        }
        return configDTO;
    }

    @Override
    public Boolean delete(Long configId) {
        return configRepository.delete(configId);
    }

    @Override
    public ConfigDTO update(Long configId, ConfigDTO configDTO) {
        configDTO.setIsDefault(false);
        configDTO.setSource(null);
        return ConvertHelper.convert(configRepository.update(configId, ConvertHelper.convert(configDTO, ConfigDO.class)), ConfigDTO.class);
    }

    @ConfigNotifyRefresh
    @Override
    public ConfigDTO updateConfig(Long configId, ConfigDTO configDTO, String type) {
        if (!StringUtils.isEmpty(type) && !StringUtils.isEmpty(configDTO.getTxt())) {
            try {
                configDTO.setValue(ConfigUtil.convertTextToMap(type, configDTO.getTxt()));
            } catch (IOException e) {
                throw new CommonException("error.config.txt");
            }
        }
        return update(configId, configDTO);
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
    public ConfigDTO create(CreateConfigDTO ccd) {
        ServiceDO serviceDO = serviceRepository.getService(ccd.getServiceName());
        if (serviceDO == null) {
           throw new CommonException("error.config.serviceName.notExist");
        }
        try {
            ConfigDO configDO = new ConfigDO();
            configDO.setName(ccd.getName());
            configDO.setServiceId(serviceDO.getId());
            configDO.setConfigVersion(ccd.getVersion());
            Map<String, Object> value = ConfigUtil.convertTextToMap(CONFIG_TYPE_YAML, ccd.getYaml());
            configDO.setValue(mapper.writeValueAsString(value));
            return ConvertHelper.convert(configRepository.create(configDO), ConfigDTO.class);
        } catch (IOException e) {
            throw new CommonException("error.config.yml");
        }
    }

    @Override
    @ConfigNotifyRefresh
    public ItemDto saveItem(Long configId, ItemDto item) {
        if (item == null || StringUtil.isEmpty(item.getProperty()) || StringUtil.isEmpty(item.getValue())) {
            throw new CommonException("error.config.item.add");
        }
        ConfigDTO configDTO = query(configId, null);
        Map<String, Object> itemMap = configDTO.getValue();
        if (checkNeedUpdate(itemMap, item)) {
            itemMap.put(item.getProperty(), item.getValue());
            configDTO.setValue(itemMap);
            if (update(configDTO.getId(), configDTO) == null) {
                throw new CommonException("error.config.item.add");
            }
        }
        return item;
    }

    @Override
    @ConfigNotifyRefresh
    public void deleteItem(Long configId, String property) {
        if (StringUtil.isEmpty(property)) {
            throw new CommonException("error.config.item.update");
        }
        ConfigDTO configDTO = query(configId, null);
        Map<String, Object> itemMap = configDTO.getValue();
        Set<String> keySet = itemMap.keySet();
        if (!keySet.contains(property)) {
            throw new CommonException("error.config.item.not.exist");
        }
        itemMap.remove(property);
        update(configDTO.getId(), configDTO);
    }

    private boolean checkNeedUpdate(Map<String, Object> map, ItemDto item) {
        String key = item.getProperty();
        String value = item.getValue();
        return !map.containsKey(key) || !value.equals(map.get(key));
    }

    @Override
    @SuppressWarnings("unchecked")
    public YamlDto queryYaml(Long configId) {
        ConfigDO configDO = configRepository.query(configId);
        if (configDO == null) {
            throw new CommonException("error.config.not.exist");
        }
        try {
            YamlDto yamlDto = new YamlDto();
            Map<String, Object> map = mapper.readValue(configDO.getValue(), Map.class);
            String yaml = ConfigUtil.convertMapToText(map, CONFIG_TYPE_YAML);
            yamlDto.setYaml(yaml);
            yamlDto.setTotalLine(ConfigUtil.appearNumber(yaml, "\n") + 1);
            return yamlDto;
        } catch (IOException e) {
            throw new CommonException("error.config.parser");
        }
    }
}
