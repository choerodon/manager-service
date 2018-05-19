package io.choerodon.manager.domain.service;

import java.util.List;

import org.apache.commons.collections.map.MultiKeyMap;

import io.choerodon.core.domain.Page;
import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.manager.domain.manager.entity.RouteE;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 路由信息操作业务service
 *
 * @author wuguokai
 */
public interface IRouteService {

    /**
     * 分页获取所有路由信息
     *
     * @return page
     */
    Page<RouteE> pageAll(PageRequest pageRequest);

    /**
     * 批量添加路由信息
     *
     * @param routeE 路由信息对象集合
     * @return List
     */
    List<RouteE> addRoutes(List<RouteE> routeE);

    /**
     * 获取所有路由信息的列表
     *
     * @return list
     */
    List<RouteE> getAll();

    /**
     * 获取所有正在运行实例的ZuulRoute
     *
     * @return 正在运行实例的map (serviceId, version):(zuulRoute)
     */
    MultiKeyMap getAllRunningInstances();

    /**
     * 从MultiKeyMap中根据那么查找ZuulRoute
     *
     * @param runningMap 查找范围
     * @param name       形如：uaa
     * @param version    版本号
     * @return 查找的ZuulRoute
     */
    RouteE getRouteFromRunningInstancesMap(MultiKeyMap runningMap, String name, String version);

    void autoRefreshRoute(String swaggerJson);

    ChoerodonRouteData fetchRouteData(String service, String version);

    RouteE queryRouteByService(String service);
}
