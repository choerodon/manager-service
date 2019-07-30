package io.choerodon.manager.infra.mapper;

import io.choerodon.manager.infra.dto.RouteDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RouteMapper extends Mapper<RouteDTO> {

    List<RouteDTO> selectRoutes(@Param("routeDTO") RouteDTO routeDTO, @Param("params") String params);

}
