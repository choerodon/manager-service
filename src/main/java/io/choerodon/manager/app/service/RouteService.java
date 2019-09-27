package io.choerodon.manager.app.service;

import io.choerodon.manager.infra.dto.RouteDTO;
import org.apache.commons.collections.map.MultiKeyMap;

/**
 * 路由信息应用层服务
 *
 * @author wuguokai
 */
public interface RouteService {

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
    RouteDTO getRouteFromRunningInstancesMap(MultiKeyMap runningMap, String name, String version);
}
