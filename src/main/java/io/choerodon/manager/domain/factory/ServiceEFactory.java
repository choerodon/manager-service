package io.choerodon.manager.domain.factory;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.manager.domain.manager.entity.ServiceE;

/**
 * @author superleader8@gmail.com
 * @author wuguokai
 */
public class ServiceEFactory {

    public static ServiceE createServiceE() {
        return ApplicationContextHelper.getSpringFactory().getBean(ServiceE.class);
    }
}
