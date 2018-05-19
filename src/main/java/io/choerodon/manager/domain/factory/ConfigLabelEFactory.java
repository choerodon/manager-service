package io.choerodon.manager.domain.factory;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.manager.domain.manager.entity.ConfigLabelE;

/**
 * configLabel对象的工厂
 *
 * @author wuguokai
 */
public class ConfigLabelEFactory {
    public static ConfigLabelE create() {
        return ApplicationContextHelper.getSpringFactory().getBean(ConfigLabelE.class);
    }
}
