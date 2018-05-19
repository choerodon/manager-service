package io.choerodon.manager.infra.common.utils;

import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.swagger.annotation.ChoerodonExtraData;
import io.choerodon.swagger.custom.extra.ExtraData;
import io.choerodon.swagger.custom.extra.ExtraDataManager;

/**
 * @author wuguokai
 */
@ChoerodonExtraData
public class CustomExtraDataManager implements ExtraDataManager {
    @Override
    public ExtraData getData() {
        ChoerodonRouteData choerodonRouteData = new ChoerodonRouteData();
        choerodonRouteData.setName("manager");
        choerodonRouteData.setPath("/manager/**");
        choerodonRouteData.setServiceId("manager-service");
        extraData.put(ExtraData.ZUUL_ROUTE_DATA, choerodonRouteData);
        return extraData;
    }
}
