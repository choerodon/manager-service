package io.choerodon.manager.app.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import io.choerodon.manager.api.dto.RouteDTO;
import io.choerodon.manager.infra.dataobject.RouteDO;

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
    PageInfo<RouteDTO> list(int page,int size, RouteDO routeDO, String params);

    /**
     * 添加一个路由
     *
     * @param routeDTO 路由对象
     * @return routeDTO
     */
    RouteDTO  create(RouteDTO routeDTO);

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

    /**
     * 批量添加路由
     *
     * @param routeDTOList 路由对象集合
     * @return list
     */
    List<RouteDTO> addRoutesBatch(List<RouteDTO> routeDTOList);

    /**
     * 获取所有路由对象
     *
     * @return list
     */
    List<RouteDTO> getAllRoute();

    RouteDTO queryByName(String name);


    void checkRoute(RouteDTO routeDTO);
}
