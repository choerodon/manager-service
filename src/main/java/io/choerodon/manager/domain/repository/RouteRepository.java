package io.choerodon.manager.domain.repository;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.manager.domain.manager.entity.RouteE;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

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

    Page<RouteE> pageAllRoutes(PageRequest pageRequest);
}
