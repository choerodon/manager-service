package io.choerodon.manager.infra.repository.impl;

import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.domain.manager.entity.ConfigLabelE;
import io.choerodon.manager.domain.repository.ConfigLabelRepository;
import io.choerodon.manager.infra.dataobject.ConfigLabelDO;
import io.choerodon.manager.infra.mapper.ConfigLabelMapper;

/**
 * @author wuguokai
 */
@Component
public class ConfigLabelRepositoryImpl implements ConfigLabelRepository {

    private ConfigLabelMapper configLabelMapper;

    public ConfigLabelRepositoryImpl(ConfigLabelMapper configLabelMapper) {
        this.configLabelMapper = configLabelMapper;
    }

    @Override
    public ConfigLabelE addConfigLabel(ConfigLabelE configLabelE) {
        ConfigLabelDO configLabelDO = ConvertHelper.convert(configLabelE, ConfigLabelDO.class);
        int isInsert = configLabelMapper.insert(configLabelDO);
        if (isInsert != 1) {
            throw new CommonException("error.configLabel.insert");
        }
        return ConvertHelper.convert(configLabelDO, ConfigLabelE.class);
    }
}
