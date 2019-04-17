package io.choerodon.manager.domain.service;

public interface IActuatorRefreshService {
    /**
     * 插入或者更新Actuator数据到表中
     * @param serviceName 服务名称
     * @param serviceVersion 服务版本
     * @param json Json数据
     * @return 成功返回true， 已经存在相同内容返回false
     */
    boolean updateOrInsertActuator(String serviceName, String serviceVersion, String json);

    void sendEvent(String json);
}
