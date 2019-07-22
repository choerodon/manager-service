package io.choerodon.manager.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.NotExistedException;
import io.choerodon.core.exception.ext.UpdateExcetion;
import io.choerodon.manager.api.dto.*;
import io.choerodon.manager.app.service.ConfigService;
import io.choerodon.manager.domain.manager.entity.RouteE;
import io.choerodon.manager.domain.manager.entity.ServiceE;
import io.choerodon.manager.domain.repository.ConfigRepository;
import io.choerodon.manager.domain.repository.RouteRepository;
import io.choerodon.manager.domain.repository.ServiceRepository;
import io.choerodon.manager.infra.common.annotation.ConfigNotifyRefresh;
import io.choerodon.manager.infra.common.utils.config.ConfigUtil;
import io.choerodon.manager.infra.conventer.ConfigConverter;
import io.choerodon.manager.infra.dto.ConfigDTO;
import io.choerodon.manager.infra.dto.ServiceDTO;
import io.choerodon.manager.infra.mapper.ConfigMapper;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

/**
 * @author wuguokai
 */
@Component
public class ConfigServiceImpl implements ConfigService {

    private final ObjectMapper mapper = new ObjectMapper();

    public static final String CONFIG_TYPE_PROPERTIES = "properties";

    public static final String CONFIG_TYPE_YAML = "yaml";

    private static final String ERROR_SERVICENAME_NOTEXIST = "error.config.serviceName.notExist";

    @Value("${choerodon.gateway.names}")
    private String[] getRouteServices;

    private ConfigRepository configRepository;

    private ServiceRepository serviceRepository;

    private RouteRepository routeRepository;

    private ConfigMapper configMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigServiceImpl.class);

    public ConfigServiceImpl(ConfigRepository configRepository,
                             ServiceRepository serviceRepository,
                             RouteRepository routeRepository,
                             ConfigMapper configMapper) {
        this.configRepository = configRepository;
        this.serviceRepository = serviceRepository;
        this.routeRepository = routeRepository;
        this.configMapper = configMapper;
    }

    public void setGetRouteServices(String[] getRouteServices) {
        this.getRouteServices = getRouteServices;
    }

    @Override
    public PageInfo<ConfigVO> listByServiceName(String serviceName, PageRequest pageRequest, ConfigDTO configDTO, String queryParam) {
        PageInfo<ConfigDTO> pageInfo =
                PageHelper
                        .startPage(pageRequest.getPage(), pageRequest.getSize())
                        .doSelectPageInfo(() -> configMapper.fulltextSearch(configDTO, serviceName, queryParam));
        return ConfigConverter.dto2Vo(pageInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConfigVO updateConfigDefault(Long configId) {
        ConfigDTO dto = configMapper.selectByPrimaryKey(configId);
        if (dto == null) {
            throw new NotExistedException("error.config.not.existed");
        }
        boolean isDefault = dto.getIsDefault();
        if (!isDefault) {
            ConfigDTO example = new ConfigDTO();
            example.setServiceId(dto.getServiceId());
            example.setIsDefault(true);
            ConfigDTO defaultDto = configMapper.selectOne(example);
            if (defaultDto != null) {
                defaultDto.setIsDefault(false);
                if (configMapper.updateByPrimaryKeySelective(defaultDto) != 1) {
                    throw new UpdateExcetion("error.config.update");
                }
            }
            dto.setIsDefault(true);
            if (configMapper.updateByPrimaryKeySelective(dto) != 1) {
                throw new UpdateExcetion("error.config.update");
            }
        }
        return ConfigConverter.dto2Vo(dto);
    }

    @Override
    public ConfigVO query(Long configId, String type) {
        ConfigVO configVO = ConvertHelper.convert(configRepository.query(configId), ConfigVO.class);
        if (configVO == null) {
            throw new CommonException("error.config.not.exist");
        }
        ServiceE serviceE = serviceRepository.getService(configVO.getServiceId());
        if (serviceE == null) {
            throw new CommonException("error.service.notExist", configVO.getServiceId());
        }
        if (!StringUtils.isEmpty(type)) {
            configVO.setTxt(ConfigUtil.convertMapToText(configVO.getValue(), type));
        }
        return configVO;
    }

    @Override
    public Boolean delete(Long configId) {
        return configRepository.delete(configId);
    }

    @Override
    public ConfigVO update(Long configId, ConfigVO configVO) {
        configVO.setIsDefault(null);
        configVO.setSource(null);
        return ConvertHelper.convert(configRepository.update(configId,
                ConvertHelper.convert(configVO, ConfigDTO.class)), ConfigVO.class);
    }

    @ConfigNotifyRefresh
    @Override
    public ConfigVO updateConfig(Long configId, ConfigVO configVO, String type) {
        if (!StringUtils.isEmpty(type) && !StringUtils.isEmpty(configVO.getTxt())) {
            try {
                Map<String, Object> map = ConfigUtil.convertTextToMap(type, configVO.getTxt());
                configVO.setValue(removeZuulRoute(map));
            } catch (IOException e) {
                throw new CommonException("error.config.txt");
            }
        }
        return update(configId, configVO);
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
    public ConfigVO queryDefaultByServiceName(String serviceName) {
        ConfigVO configVO = configRepository.queryDefaultByServiceName(serviceName);
        if (configVO == null) {
            LOGGER.info("$${}$$", serviceName);
            throw new CommonException("error.serviceConfigDO.query.serviceNameNotFound");
        }
        if (ArrayUtils.contains(getRouteServices, serviceName)) {
            final List<RouteE> routeEList = routeRepository.getAllRoute();
            setRoutes(routeEList, configVO.getValue());
        }
        return configVO;
    }


    @Override
    public ConfigVO queryByServiceNameAndConfigVersion(String serviceName, String configVersion) {
        ConfigVO configVO =
                configRepository.queryByServiceNameAndConfigVersion(serviceName, configVersion);
        if (configVO == null) {
            throw new CommonException("error.serviceConfigDO.query.serviceNameOrConfigVersionNotFound");
        }
        if (ArrayUtils.contains(getRouteServices, serviceName)) {
            final List<RouteE> routeEList = routeRepository.getAllRoute();
            setRoutes(routeEList, configVO.getValue());
        }
        return configVO;
    }

    @Override
    public ConfigVO create(CreateConfigDTO ccd) {
        ServiceDTO serviceDTO = serviceRepository.getService(ccd.getServiceName());
        if (serviceDTO == null) {
            throw new CommonException(ERROR_SERVICENAME_NOTEXIST);
        }
        try {
            ConfigDTO configDTO = new ConfigDTO();
            configDTO.setName(ccd.getName());
            configDTO.setServiceId(serviceDTO.getId());
            configDTO.setConfigVersion(ccd.getVersion());
            Map<String, Object> value = ConfigUtil.convertTextToMap(CONFIG_TYPE_YAML, ccd.getYaml());
            configDTO.setValue(mapper.writeValueAsString(removeZuulRoute(value)));
            return ConvertHelper.convert(configRepository.create(configDTO), ConfigVO.class);
        } catch (IOException e) {
            throw new CommonException("error.config.yml");
        }
    }

    private Map<String, Object> removeZuulRoute(final Map<String, Object> value) {
        Map<String, Object> newValue = new HashMap<>(value.size());
        for (Map.Entry<String, Object> entry : value.entrySet()) {
            if (!entry.getKey().startsWith("zuul.routes.")) {
                newValue.put(entry.getKey(), entry.getValue());
            }
        }
        return newValue;
    }

    @Override
    @ConfigNotifyRefresh
    public ItemDto saveItem(Long configId, ItemDto item) {
        if (item == null || StringUtils.isEmpty(item.getProperty()) || StringUtils.isEmpty(item.getValue())) {
            throw new CommonException("error.config.item.add");
        }
        ConfigVO configVO = query(configId, null);
        Map<String, Object> itemMap = configVO.getValue();
        if (checkNeedUpdate(itemMap, item)) {
            itemMap.put(item.getProperty(), item.getValue());
            configVO.setValue(itemMap);
            if (update(configVO.getId(), configVO) == null) {
                throw new CommonException("error.config.item.add");
            }
        }
        return item;
    }

    @Override
    @ConfigNotifyRefresh
    public void deleteItem(Long configId, String property) {
        if (StringUtils.isEmpty(property)) {
            throw new CommonException("error.config.item.update");
        }
        ConfigVO configVO = query(configId, null);
        Map<String, Object> itemMap = configVO.getValue();
        Set<String> keySet = itemMap.keySet();
        if (!keySet.contains(property)) {
            throw new CommonException("error.config.item.not.exist");
        }
        itemMap.remove(property);
        update(configVO.getId(), configVO);
    }

    private boolean checkNeedUpdate(Map<String, Object> map, ItemDto item) {
        String key = item.getProperty();
        String value = item.getValue();
        return !map.containsKey(key) || !value.equals(map.get(key));
    }

    @Override
    @SuppressWarnings("unchecked")
    public YamlDTO queryYaml(Long configId) {
        ConfigDTO configDTO = configRepository.query(configId);
        if (configDTO == null) {
            throw new CommonException("error.config.not.exist");
        }
        try {
            Map<String, Object> map = mapper.readValue(configDTO.getValue(), Map.class);
            ServiceE serviceE = serviceRepository.getService(configDTO.getServiceId());
            if (serviceE == null) {
                throw new CommonException("error.config.service.not.exist");
            }
            YamlDTO yamlDTO = new YamlDTO();
            String yaml = ConfigUtil.convertMapToText(map, CONFIG_TYPE_YAML);
            yamlDTO.setObjectVersionNumber(configDTO.getObjectVersionNumber());
            yamlDTO.setYaml(yaml);
            yamlDTO.setTotalLine(ConfigUtil.appearNumber(yaml, "\n") + 1);
            return yamlDTO;
        } catch (IOException e) {
            throw new CommonException("error.config.parser");
        }
    }

    @Override
    public void check(ConfigCheckDTO configDTO) {
        if (configDTO == null) {
            return;
        }
        if (configDTO.getServiceName() == null) {
            throw new CommonException(ERROR_SERVICENAME_NOTEXIST);
        }
        ServiceDTO serviceDTO = serviceRepository.getService(configDTO.getServiceName());
        if (serviceDTO == null) {
            throw new CommonException(ERROR_SERVICENAME_NOTEXIST);
        }
        if (configDTO.getConfigVersion() != null) {
            ConfigDTO configDO = configRepository.queryByServiceIdAndVersion(
                    serviceDTO.getId(), configDTO.getConfigVersion());
            if (configDO != null) {
                throw new CommonException("error.config.insert.versionDuplicate");
            }
        }
        if (configDTO.getName() != null) {
            ConfigDTO configDO = configRepository.queryByServiceIdAndName(
                    serviceDTO.getId(), configDTO.getName());
            if (configDO != null) {
                throw new CommonException("error.config.insert.nameDuplicate");
            }
        }
    }
}
