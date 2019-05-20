package io.choerodon.manager.domain.service;

import io.choerodon.eureka.event.EurekaEventPayload;

/**
 * 更新swagger业务service
 *
 * @author zhipeng.zuo
 * @author wuguokai
 */
public interface ISwaggerRefreshService {

    /**
     * 更新或者插入swagger json
     *
     * @param registerInstancePayload 存储service信息的实体
     * @param json                    swagger json
     */
    void updateOrInsertSwagger(EurekaEventPayload registerInstancePayload, String json);

}
