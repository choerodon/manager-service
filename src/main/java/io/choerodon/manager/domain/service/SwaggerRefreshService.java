package io.choerodon.manager.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.choerodon.manager.api.dto.RegisterInstancePayload;

/**
 * 更新swagger业务service
 *
 * @author zhipeng.zuo
 * @author wuguokai
 */
public interface SwaggerRefreshService {

    /**
     * 更新或者插入swagger json
     *
     * @param registerInstancePayload 存储service信息的实体
     * @param json                    swagger json
     */
    void updateOrInsertSwagger(RegisterInstancePayload registerInstancePayload, String json);

    /**
     * 通过iam-service刷新权限
     *
     * @param registerInstancePayload 存储service信息的实体
     * @param json                    swagger json
     */
    void parsePermission(RegisterInstancePayload registerInstancePayload, String json) throws JsonProcessingException;

}
