package io.choerodon.manager.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.manager.api.dto.swagger.ControllerDTO;
import io.choerodon.manager.infra.dataobject.Sort;

import java.util.Map;
import java.util.Set;

/**
 * @author superlee
 */
public interface ApiService {

    PageInfo<ControllerDTO> getControllers(String name, String version, int page, int size, Sort sort, Map<String, Object> map);

    ControllerDTO queryPathDetail(String serviceName, String version, String controllerName, String operationId);

    /**
     * 查询正在运行实例的接口数量，目前只查询单一版本的个数，如果后期支持多版本部署，则应带上版本去查询数量
     *
     * @return Map
     */
    Map<String, Object> queryInstancesAndApiCount();

    /**
     * 根据route name和version获取swagger json
     *
     * @param name    route name
     * @param version instance version
     * @return String
     */
    String getSwaggerJson(String name, String version);

    /**
     * 根据起始日期和结束日期查询服务的调用次数，显示的服务为正在运行的服务以及日期范围内被调用过的服务
     * @param beginDate
     * @param endDate
     * @return
     */
    Map<String, Object> queryServiceInvoke(String beginDate, String endDate);

    /**
     * 根据日期范围和服务名在redis中查询api调用次数
     *
     * @param beginDate     开始日期
     * @param endDate       结束日期
     * @param additionalKey 和日期拼接的额外的key
     * @param paramKey      api或者service集合的参数名
     * @param additionalParamValues 额外的paramValues
     * @return map
     */
    Map<String, Object> queryInvokeCount(String beginDate, String endDate, String additionalKey, String paramKey, Set<String> additionalParamValues);

    /**
     * 查询所有运行实例的api树形接口
     *
     * @return map
     */
    Map queryTreeMenu();
}
