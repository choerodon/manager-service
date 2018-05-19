package io.choerodon.manager.domain.factory;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.manager.domain.manager.entity.GroupLabelE;

/**
 * {@inheritDoc}
 *
 * @author superleader8@gmail.com
 * @data 2018/3/14
 */
public class GroupLabelEFactory {

    public static GroupLabelE createGroupLabelE() {
        return ApplicationContextHelper.getSpringFactory().getBean(GroupLabelE.class);
    }
}
