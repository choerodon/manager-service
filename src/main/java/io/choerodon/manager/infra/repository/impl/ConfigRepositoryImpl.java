package io.choerodon.manager.infra.repository.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.ConfigVO;
import io.choerodon.manager.domain.repository.ConfigRepository;
import io.choerodon.manager.infra.dto.ConfigDTO;
import io.choerodon.manager.infra.mapper.ConfigMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
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
    public PageInfo<ConfigVO> listByServiceName(String serviceName, int page, int size, ConfigDTO queryInfo, String queryParam) {
        PageInfo<ConfigDTO> pageInfo =
                PageHelper.startPage(page, size).doSelectPageInfo(() -> configMapper.fulltextSearch(queryInfo, serviceName, queryParam));
        Page<ConfigVO> result = new Page<>(page, size);
        result.setTotal(pageInfo.getTotal());
        List<ConfigVO> configs = new ArrayList<>();
        pageInfo.getList().forEach(c -> {
            ConfigVO dto = new ConfigVO();
            BeanUtils.copyProperties(c, dto);
            configs.add(dto);
        });
        result.addAll(configs);
        return result.toPageInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConfigDTO setConfigDefault(Long configId) {
        ConfigDTO configDTO = configMapper.selectByPrimaryKey(configId);
        if (configDTO == null) {
            throw new CommonException(ERROR_CONFIG_NOT_EXIST);
        }

        ConfigDTO oldDefaultConfig = configMapper.selectOne(new ConfigDTO(true, configDTO.getServiceId()));
        if (oldDefaultConfig != null) {
            oldDefaultConfig.setIsDefault(false);
            if (configMapper.updateByPrimaryKeySelective(oldDefaultConfig) != 1) {
                throw new CommonException("error.config.set.default");
            }
        }
        configDTO.setIsDefault(true);
        if (configMapper.updateByPrimaryKeySelective(configDTO) != 1) {
            throw new CommonException("error.config.set.default");
        }
        return configMapper.selectByPrimaryKey(configId);
    }

    @Override
    public ConfigDTO query(Long serviceConfigId) {
        return configMapper.selectByPrimaryKey(serviceConfigId);
    }

    @Override
    public boolean delete(Long configId) {
        ConfigDTO configDTO = configMapper.selectByPrimaryKey(configId);
        if (configMapper.selectByPrimaryKey(configId) == null) {
            throw new CommonException(ERROR_CONFIG_NOT_EXIST);
        }
        if (configDTO.getIsDefault()) {
            throw new CommonException("error.config.delete.default");
        }
        if (configMapper.deleteByPrimaryKey(configId) != 1) {
            throw new CommonException("error.config.delete");
        }
        return true;
    }

    @Override
    public ConfigDTO update(Long configId, ConfigDTO configDTO) {
        if (configDTO.getObjectVersionNumber() == null) {
            throw new CommonException("error.objectVersionNumber.null");
        }
        if (configMapper.selectByPrimaryKey(configId) == null) {
            throw new CommonException(ERROR_CONFIG_NOT_EXIST);
        }
        configDTO.setId(configId);
        if (configMapper.updateByPrimaryKeySelective(configDTO) != 1) {
            throw new CommonException("error.config.update");
        }
        return configMapper.selectByPrimaryKey(configDTO.getId());
    }

    @Override
    public ConfigVO queryDefaultByServiceName(String serviceName) {
        ConfigDTO config = null;
        List<ConfigDTO> configs = configMapper.selectByServiceDefault(serviceName);
        if (!configs.isEmpty()) {
            config = configs.get(0);
        }
        return ConvertHelper.convert(config, ConfigVO.class);
    }

    @Override
    public ConfigVO queryByServiceNameAndConfigVersion(String serviceName, String configVersion) {
        List<ConfigDTO> configs = configMapper.selectByServiceAndConfigVersion(serviceName, configVersion);
        ConfigDTO config = null;
        if (!configs.isEmpty()) {
            config = configs.get(0);
        }
        return ConvertHelper.convert(config, ConfigVO.class);
    }

    @Override
    public ConfigDTO create(ConfigDTO configDTO) {
        configDTO.setIsDefault(false);
        configDTO.setSource(SOURCE_PAGE);
        if (configDTO.getPublicTime() == null) {
            configDTO.setPublicTime(new Date(System.currentTimeMillis()));
        }
        if (configMapper.insert(configDTO) != 1) {
            throw new CommonException("error.config.create");
        }
        return configMapper.selectByPrimaryKey(configDTO.getId());
    }

    @Override
    public ConfigDTO queryByServiceIdAndVersion(Long serviceId, String configVersion) {
        ConfigDTO configDTO = new ConfigDTO();
        configDTO.setServiceId(serviceId);
        configDTO.setConfigVersion(configVersion);
        return configMapper.selectOne(configDTO);
    }

    @Override
    public ConfigDTO queryByServiceIdAndName(Long serviceId, String name) {
        ConfigDTO configDTO = new ConfigDTO();
        configDTO.setServiceId(serviceId);
        configDTO.setName(name);
        return configMapper.selectOne(configDTO);
    }
}
