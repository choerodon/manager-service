package io.choerodon.manager.domain.repository;

import io.choerodon.manager.domain.manager.entity.ConfigLabelE;

/**
 * {@inheritDoc}
 *
 * @author wuguokai
 */
public interface ConfigLabelRepository {
    /**
     * 新建一个config对象
     *
     * @param configLabelE 领域对象
     * @return config
     */
    ConfigLabelE addConfigLabel(ConfigLabelE configLabelE);
}
