package io.choerodon.manager.domain.repository;

import java.util.List;

import com.github.pagehelper.PageInfo;
import io.choerodon.manager.domain.manager.entity.RouteE;
import io.choerodon.manager.infra.dto.RouteDTO;

/**
 * {@inheritDoc}
 *
 * @author wuguokai
 */
public interface RouteRepository {

    RouteE queryRoute(RouteE routeE);

    RouteE addRoute(RouteE routeE);

    RouteE updateRoute(RouteE routeE);

    boolean deleteRoute(RouteE routeE);

    List<RouteE> getAllRoute();

    List<RouteE> addRoutesBatch(List<RouteE> routeEList);

    PageInfo<RouteDTO> pageAllRoutes(int page, int size, RouteDTO routeDTO, String params);

    int countRoute(RouteDTO routeDTO);

    void delete(Long routeId);
}
