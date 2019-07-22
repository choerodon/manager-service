package io.choerodon.manager.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.manager.domain.manager.entity.RouteE;
import io.choerodon.manager.infra.dto.RouteDTO;
import org.apache.commons.collections.map.MultiKeyMap;

/**
 * 路由信息应用层服务
 *
 * @author wuguokai
 */
public interface RouteService {
    /**
     * 分页获取所有路由信息
     *
     * @return page
     */
    PageInfo<RouteDTO> list(PageRequest pageRequest, RouteDTO routeDTO, String params);

    /**
     * 添加一个路由
     *
     * @param routeDTO 路由对象
     * @return routeDTO
     */
    RouteDTO create(RouteDTO routeDTO);

    /**
     * 更新一个路由对象
     *
     * @param routeDTO 路由对象
     * @return routeDTO
     */
    RouteDTO update(Long id, RouteDTO routeDTO);

    /**
     * 删除一个路由对象
     *
     * @param routeId 路由id
     * @return boolean
     */
    void delete(Long routeId);


    void checkRoute(RouteDTO routeDTO);

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
}
