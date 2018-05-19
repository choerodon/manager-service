package io.choerodon.manager.domain.factory;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.manager.domain.manager.entity.RouteE;

/**
 * {@inheritDoc}
 *
 * @author superleader8@gmail.com
 * @author wuguokai
 */
public class RouteEFactory {
    public static RouteE createRouteE() {
        return ApplicationContextHelper.getSpringFactory().getBean(RouteE.class);
    }
}


