package io.choerodon.manager.infra.repository.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.ConfigDTO;
import io.choerodon.manager.domain.repository.ConfigRepository;
import io.choerodon.manager.infra.dataobject.ConfigDO;
import io.choerodon.manager.infra.mapper.ConfigMapper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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
    public Page<ConfigDTO> listByServiceName(String serviceName, PageRequest pageRequest, ConfigDO queryInfo, String queryParam) {
        Page<ConfigDO> configDOPage =
                PageHelper.doPageAndSort(pageRequest, () -> configMapper.fulltextSearch(queryInfo, serviceName, queryParam));
        return ConvertPageHelper.convertPage(configDOPage, ConfigDTO.class);
    }

    @Override
    public Page<ConfigDTO> list(PageRequest pageRequest) {
        return ConvertPageHelper.convertPage(PageHelper.doPageAndSort(pageRequest,
                () -> configMapper.selectAll()), ConfigDTO.class);
    }

    @Override
    @Transactional
    public ConfigDO setConfigDefault(Long configId) {
        ConfigDO configDO = configMapper.selectByPrimaryKey(configId);
        if (configDO == null) {
            throw new CommonException(ERROR_CONFIG_NOT_EXIST);
        }

        ConfigDO oldDefaultConfig = configMapper.selectOne(new ConfigDO(true, configDO.getServiceId()));
        if (oldDefaultConfig != null) {
            oldDefaultConfig.setIsDefault(false);
            if (configMapper.updateByPrimaryKeySelective(oldDefaultConfig) != 1) {
                throw new CommonException("error.config.set.default");
            }
        }
        configDO.setIsDefault(true);
        if (configMapper.updateByPrimaryKeySelective(configDO) != 1) {
            throw new CommonException("error.config.set.default");
        }
        return configMapper.selectByPrimaryKey(configId);
    }

    @Override
    public ConfigDO query(Long serviceConfigId) {
        return configMapper.selectByPrimaryKey(serviceConfigId);
    }

    @Override
    public boolean delete(Long configId) {
        ConfigDO configDO = configMapper.selectByPrimaryKey(configId);
        if (configMapper.selectByPrimaryKey(configId) == null) {
            throw new CommonException(ERROR_CONFIG_NOT_EXIST);
        }
        if (configDO.getIsDefault()) {
            throw new CommonException("error.config.delete.default");
        }
        if (configMapper.deleteByPrimaryKey(configId) != 1) {
            throw new CommonException("error.config.delete");
        }
        return true;
    }

    @Override
    public ConfigDO update(Long configId, ConfigDO configDO) {
        if (configDO.getObjectVersionNumber() == null) {
            throw new CommonException("error.objectVersionNumber.null");
        }
        if (configMapper.selectByPrimaryKey(configId) == null) {
            throw new CommonException(ERROR_CONFIG_NOT_EXIST);
        }
        configDO.setId(configId);
        if (configMapper.updateByPrimaryKeySelective(configDO) != 1) {
            throw new CommonException("error.config.update");
        }
        return configMapper.selectByPrimaryKey(configDO.getId());
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
    public ConfigDO create(ConfigDO configDO) {
        configDO.setIsDefault(false);
        configDO.setSource(SOURCE_PAGE);
        if (configDO.getPublicTime() == null) {
            configDO.setPublicTime(new Date(System.currentTimeMillis()));
        }
        if (configMapper.insert(configDO) != 1) {
            throw new CommonException("error.config.create");
        }
        return configMapper.selectByPrimaryKey(configDO.getId());
    }
}
