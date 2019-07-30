package io.choerodon.manager.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.NotExistedException;
import io.choerodon.core.exception.ext.UpdateExcetion;
import io.choerodon.manager.api.dto.*;
import io.choerodon.manager.app.service.ConfigService;
import io.choerodon.manager.infra.asserts.ConfigAssertHelper;
import io.choerodon.manager.infra.common.annotation.ConfigNotifyRefresh;
import io.choerodon.manager.infra.common.utils.config.ConfigUtil;
import io.choerodon.manager.infra.conventer.ConfigConverter;
import io.choerodon.manager.infra.dto.ConfigDTO;
import io.choerodon.manager.infra.dto.RouteDTO;
import io.choerodon.manager.infra.dto.ServiceDTO;
import io.choerodon.manager.infra.mapper.ConfigMapper;
import io.choerodon.manager.infra.mapper.RouteMapper;
import io.choerodon.manager.infra.mapper.ServiceMapper;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final String ERROR_SERVICE_NAME_NOT_EXIST = "error.config.serviceName.notExist";

    @Value("${choerodon.gateway.names}")
    private String[] getRouteServices;

    private ConfigMapper configMapper;

    private ServiceMapper serviceMapper;

    private ConfigAssertHelper configAssertHelper;

    private RouteMapper routeMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigServiceImpl.class);

    public ConfigServiceImpl(ConfigMapper configMapper,
                             ServiceMapper serviceMapper,
                             ConfigAssertHelper configAssertHelper,
                             RouteMapper routeMapper) {
        this.configMapper = configMapper;
        this.serviceMapper = serviceMapper;
        this.configAssertHelper = configAssertHelper;
        this.routeMapper = routeMapper;
        this.serviceMapper = serviceMapper;
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
        ConfigDTO dto = configAssertHelper.notExisted(configId);
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
        ConfigDTO configDTO = configAssertHelper.notExisted(configId);
        ConfigVO configVO = ConfigConverter.dto2Vo(configDTO);
        Long serviceId = configVO.getServiceId();
        ServiceDTO serviceDTO = serviceMapper.selectByPrimaryKey(serviceId);
        if (serviceDTO == null) {
            throw new NotExistedException("error.service.notExist", serviceId);
        }
        if (!StringUtils.isEmpty(type)) {
            configVO.setTxt(ConfigUtil.convertMapToText(configVO.getValue(), type));
        }
        return configVO;
    }

    @Override
    public Boolean delete(Long configId) {
        ConfigDTO configDTO = configAssertHelper.notExisted(configId);
        if (configDTO.getIsDefault()) {
            throw new CommonException("error.config.delete.default");
        }
        configMapper.deleteByPrimaryKey(configId);
        return true;
    }

    @Override
    public ConfigVO update(Long configId, ConfigVO configVO) {
        configVO.setIsDefault(null);
        configVO.setSource(null);
        configAssertHelper.objectVersionNumberNotNull(configVO.getObjectVersionNumber());
        configAssertHelper.notExisted(configId);
        ConfigDTO configDTO = ConfigConverter.vo2Dto(configVO);
        if (configMapper.updateByPrimaryKeySelective(configDTO) != 1) {
            throw new CommonException("error.config.update");
        }
        return ConfigConverter.dto2Vo(configAssertHelper.notExisted(configId));
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
    private void setRoutes(final List<RouteDTO> routes, final Map map) {
        for (RouteDTO dto : routes) {
            String prefix = "zuul.routes." + dto.getName() + ".";
            if (dto.getPath() != null) {
                map.put(prefix + "path", dto.getPath());
            }
            if (dto.getServiceId() != null) {
                map.put(prefix + "serviceId", dto.getServiceId());
            }
            if (dto.getUrl() != null) {
                map.put(prefix + "url", dto.getUrl());
            }
            if (dto.getStripPrefix() != null) {
                map.put(prefix + "stripPrefix", dto.getStripPrefix());
            }
            if (dto.getRetryable() != null) {
                map.put(prefix + "retryable", dto.getRetryable());
            }
            if (dto.getHelperService() != null) {
                map.put(prefix + "helperService", dto.getHelperService());
            }
            if (dto.getCustomSensitiveHeaders() != null && dto.getCustomSensitiveHeaders()) {
                map.put(prefix + "customSensitiveHeaders", dto.getCustomSensitiveHeaders());
            }
            if (dto.getSensitiveHeaders() != null) {
                map.put(prefix + "sensitiveHeaders", dto.getSensitiveHeaders());
            }
        }
    }


    @Override
    public ConfigVO queryDefaultByServiceName(String serviceName) {
        ConfigDTO configDTO;
        List<ConfigDTO> configs = configMapper.selectByServiceDefault(serviceName);
        if (!configs.isEmpty()) {
            configDTO = configs.get(0);
        } else {
            LOGGER.info("$${}$$", serviceName);
            throw new CommonException("error.serviceConfigDO.query.serviceNameNotFound");
        }
        ConfigVO configVO = ConfigConverter.dto2Vo(configDTO);
        if (ArrayUtils.contains(getRouteServices, serviceName)) {
            List<RouteDTO> routes = routeMapper.selectAll();
            setRoutes(routes, configVO.getValue());
        }
        return configVO;
    }


    @Override
    public ConfigVO queryByServiceNameAndConfigVersion(String serviceName, String configVersion) {
        List<ConfigDTO> configs = configMapper.selectByServiceAndConfigVersion(serviceName, configVersion);
        ConfigDTO configDTO;
        if (!configs.isEmpty()) {
            configDTO = configs.get(0);
        } else {
            throw new CommonException("error.serviceConfigDO.query.serviceNameOrConfigVersionNotFound");
        }
        ConfigVO configVO = ConfigConverter.dto2Vo(configDTO);
        if (ArrayUtils.contains(getRouteServices, serviceName)) {
            List<RouteDTO> routes = routeMapper.selectAll();
            setRoutes(routes, configVO.getValue());
        }
        return configVO;
    }

    @Override
    public ConfigVO create(CreateConfigDTO dto) {
        ServiceDTO example = new ServiceDTO();
        example.setName(dto.getName());
        ServiceDTO serviceDTO = serviceMapper.selectOne(example);
        if (serviceDTO == null) {
            throw new CommonException(ERROR_SERVICE_NAME_NOT_EXIST);
        }
        try {
            ConfigDTO configDTO = new ConfigDTO();
            configDTO.setName(dto.getName());
            configDTO.setServiceId(serviceDTO.getId());
            configDTO.setConfigVersion(dto.getVersion());
            Map<String, Object> value = ConfigUtil.convertTextToMap(CONFIG_TYPE_YAML, dto.getYaml());
            configDTO.setValue(mapper.writeValueAsString(removeZuulRoute(value)));

            configDTO.setIsDefault(false);
            configDTO.setSource("页面生成");
            if (configDTO.getPublicTime() == null) {
                configDTO.setPublicTime(new Date(System.currentTimeMillis()));
            }
            if (configMapper.insert(configDTO) != 1) {
                throw new CommonException("error.config.create");
            }
            return ConfigConverter.dto2Vo(configMapper.selectByPrimaryKey(configDTO.getId()));
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
        ConfigDTO configDTO = configAssertHelper.notExisted(configId);
        try {
            Map<String, Object> map = mapper.readValue(configDTO.getValue(), Map.class);
            ServiceDTO serviceDTO = serviceMapper.selectByPrimaryKey(configDTO.getServiceId());
            if (serviceDTO == null) {
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
            throw new CommonException(ERROR_SERVICE_NAME_NOT_EXIST);
        }
        ServiceDTO serviceExample = new ServiceDTO();
        serviceExample.setName(configDTO.getServiceName());
        ServiceDTO serviceDTO = serviceMapper.selectOne(serviceExample);
        if (serviceDTO == null) {
            throw new CommonException(ERROR_SERVICE_NAME_NOT_EXIST);
        }
        if (configDTO.getConfigVersion() != null) {
            ConfigDTO example = new ConfigDTO();
            example.setServiceId(serviceDTO.getId());
            example.setConfigVersion(configDTO.getConfigVersion());
            ConfigDTO configDO = configMapper.selectOne(example);
            if (configDO != null) {
                throw new CommonException("error.config.insert.versionDuplicate");
            }
        }
        if (configDTO.getName() != null) {
            ConfigDTO example = new ConfigDTO();
            example.setServiceId(serviceDTO.getId());
            example.setName(configDTO.getName());
            ConfigDTO configDO = configMapper.selectOne(example);
            if (configDO != null) {
                throw new CommonException("error.config.insert.nameDuplicate");
            }
        }
    }
}
