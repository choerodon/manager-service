package io.choerodon.manager.infra.repository.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.ConfigDTO;
import io.choerodon.manager.domain.manager.entity.ConfigE;
import io.choerodon.manager.domain.repository.ConfigRepository;
import io.choerodon.manager.infra.dataobject.ConfigDO;
import io.choerodon.manager.infra.mapper.ConfigMapper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wuguokai
 */
@Component
public class ConfigRepositoryImpl implements ConfigRepository {

    private static final String SOURCE_PAGE = "页面生成";

    private static final String ERROR_CONFIG_NOT_EXIST = "error.config.item.not.exist";

    private ConfigMapper configMapper;

    public ConfigRepositoryImpl(ConfigMapper configMapper) {
        this.configMapper = configMapper;
    }

    @Override
    public Page<ConfigDTO> listByServiceId(Long serviceId, PageRequest pageRequest) {
        ConfigDO configDO = new ConfigDO();
        configDO.setServiceId(serviceId);
        Page<ConfigDO> configDOPage =
                PageHelper.doPageAndSort(pageRequest, () -> configMapper.select(configDO));
        return ConvertPageHelper.convertPage(configDOPage, ConfigDTO.class);
    }

    @Override
    public Page<ConfigDTO> list(PageRequest pageRequest) {
        return ConvertPageHelper.convertPage(PageHelper.doPageAndSort(pageRequest,
                () -> configMapper.selectAll()), ConfigDTO.class);
    }

    @Override
    @Transactional
    public ConfigE setConfigDefault(Long configId) {
        ConfigDO configDO = configMapper.selectByPrimaryKey(configId);
        if (configDO == null) {
            throw new CommonException(ERROR_CONFIG_NOT_EXIST);
        }
        ConfigE configE = ConvertHelper.convert(configDO, ConfigE.class);
        configE.setItDefault();
        configDO = ConvertHelper.convert(configE, ConfigDO.class);
        configMapper.closeDefaultByServiceId(configDO.getServiceId());
        if (configMapper.updateByPrimaryKeySelective(configDO) != 1) {
            throw new CommonException("error.config.set.default");
        }
        configDO = configMapper.selectByPrimaryKey(configDO.getId());
        return ConvertHelper.convert(configDO, ConfigE.class);
    }

    @Override
    public ConfigE query(Long serviceConfigId) {
        return ConvertHelper.convert(configMapper.selectByPrimaryKey(serviceConfigId), ConfigE.class);
    }

    @Override
    public boolean delete(Long configId) {
        ConfigDO configDO = configMapper.selectByPrimaryKey(configId);
        if (configMapper.selectByPrimaryKey(configId) == null) {
            throw new CommonException(ERROR_CONFIG_NOT_EXIST);
        }
        if (configDO.getDefault()) {
            throw new CommonException("error.config.delete.default");
        }
        if (configMapper.deleteByPrimaryKey(configId) != 1) {
            throw new CommonException("error.config.delete");
        }
        return true;
    }

    @Override
    public ConfigE update(Long configId, ConfigE configE) {
        if (configE.getObjectVersionNumber() == null) {
            throw new CommonException("error.objectVersionNumber.null");
        }
        if (configMapper.selectByPrimaryKey(configId) == null) {
            throw new CommonException(ERROR_CONFIG_NOT_EXIST);
        }
        ConfigDO configDO = ConvertHelper.convert(configE, ConfigDO.class);
        configDO.setId(configId);
        if (configMapper.updateByPrimaryKeySelective(configDO) != 1) {
            throw new CommonException("error.config.update");
        }
        configDO = configMapper.selectByPrimaryKey(configDO.getId());
        return ConvertHelper.convert(configDO, ConfigE.class);
    }

    @Override
    public ConfigDTO queryDefaultByServiceName(String serviceName) {
        return ConvertHelper.convert(configMapper.selectOneByServiceDefault(serviceName),
                ConfigDTO.class);
    }

    @Override
    public ConfigDTO queryByServiceNameAndConfigVersion(String serviceName, String configVersion) {
        return ConvertHelper.convert(configMapper.selectOneByServiceAndConfigVersion(serviceName, configVersion),
                ConfigDTO.class);
    }

    @Override
    public List<ConfigDTO> listByServiceName(String serviceName) {
        return ConvertHelper.convertList(configMapper.listByServiceName(serviceName), ConfigDTO.class);
    }

    @Override
    public ConfigDO create(ConfigDO configDO) {
        configDO.setDefault(false);
        configDO.setSource(SOURCE_PAGE);
        if (configMapper.insert(configDO) != 1) {
            throw new CommonException("error.config.create");
        }
        return configMapper.selectByPrimaryKey(configDO.getId());
    }
}
