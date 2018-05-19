package io.choerodon.manager.domain.service;

import java.util.List;

import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.UiConfiguration;

import io.choerodon.manager.api.dto.RegisterInstancePayload;

/**
 * swagger业务service
 *
 * @author zhipeng.zuo
 * @author wuguokai
 */
public interface ISwaggerService {

    /**
     * 更新或者添加swagger信息
     *
     * @param registerInstancePayload 消息队列拿到的信息实体
     * @param swaggerJson             swagger的json信息字符串
     */
    void updateOrInsertSwagger(RegisterInstancePayload registerInstancePayload, String swaggerJson);


    List<SwaggerResource> getSwaggerResource();

    UiConfiguration getUiConfiguration();

    SecurityConfiguration getSecurityConfiguration();
}
