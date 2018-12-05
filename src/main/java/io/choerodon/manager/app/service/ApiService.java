package io.choerodon.manager.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.manager.api.dto.swagger.ControllerDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.Map;

/**
 * @author superlee
 */
public interface ApiService {

    Page<ControllerDTO> getControllers(String name, String version, PageRequest pageRequest, Map<String, Object> map);

    ControllerDTO queryPathDetail(String serviceName, String version, String controllerName, String operationId);

    /**
     * 查询正在运行实例的接口数量，目前只查询单一版本的个数，如果后期支持多版本部署，则应带上版本去查询数量
     *
     * @return Map
     */
    Map queryInstancesAndApiCount();

    /**
     * 根据route name和version获取swagger json
     *
     * @param name    route name
     * @param version instance version
     * @return String
     */
    String getSwaggerJson(String name, String version);
}
