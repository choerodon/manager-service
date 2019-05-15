package io.choerodon.manager.infra.mapper;

import io.choerodon.manager.domain.manager.entity.RouteE;
import io.choerodon.manager.infra.dataobject.RouteDO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RouteMapper extends Mapper<RouteDO> {

    List<RouteE> selectRoutes(@Param("routeDO") RouteDO routeDO, @Param("params") String params);

}
