package io.choerodon.manager.domain.manager.converter;

import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.manager.domain.factory.ConfigLabelEFactory;
import io.choerodon.manager.domain.manager.entity.ConfigLabelE;
import io.choerodon.manager.infra.dataobject.ConfigLabelDO;

/**
 * {@inheritDoc}
 *
 * @author superleader8@gmail.com
 * @data 2018/3/11
 */
@Component
public class ConfigLabelConverter implements ConvertorI<ConfigLabelE, ConfigLabelDO, Object> {

    @Override
    public ConfigLabelE doToEntity(ConfigLabelDO configLabelDO) {
        ConfigLabelE cle = ConfigLabelEFactory.create();
        cle.setId(configLabelDO.getId());
        cle.setConfigId(configLabelDO.getConfigId());
        cle.setLabel(configLabelDO.getLabel());
        cle.setObjectVersionNumber(configLabelDO.getObjectVersionNumber());
        return cle;
    }

    @Override
    public ConfigLabelDO entityToDo(ConfigLabelE configLabelE) {
        ConfigLabelDO configLabelDO = new ConfigLabelDO();
        configLabelDO.setId(configLabelE.getId());
        configLabelDO.setConfigId(configLabelE.getConfigId());
        configLabelDO.setLabel(configLabelE.getLabel());
        configLabelDO.setObjectVersionNumber(configLabelE.getObjectVersionNumber());
        return configLabelDO;
    }
}
