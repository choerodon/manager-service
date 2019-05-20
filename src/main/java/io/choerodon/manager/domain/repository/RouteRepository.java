package io.choerodon.manager.domain.repository;

import java.util.List;

import com.github.pagehelper.PageInfo;
import io.choerodon.manager.domain.manager.entity.RouteE;
import io.choerodon.manager.infra.dataobject.RouteDO;

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

    PageInfo<RouteDO> pageAllRoutes(int page, int size, RouteDO routeDO, String params);

    int countRoute(RouteDO routeDO);

    void delete(Long routeId);
}
