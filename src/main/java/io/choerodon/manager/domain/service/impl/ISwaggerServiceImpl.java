package io.choerodon.manager.domain.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.UiConfiguration;

import io.choerodon.manager.api.dto.RegisterInstancePayload;
import io.choerodon.manager.domain.factory.SwaggerEFactory;
import io.choerodon.manager.domain.service.ISwaggerService;


/**
 * 实现类
 *
 * @author zhipeng.zuo
 * @author wuguokai
 */
@Service
public class ISwaggerServiceImpl implements ISwaggerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ISwaggerServiceImpl.class);

    @Override
    public void updateOrInsertSwagger(RegisterInstancePayload registerInstancePayload, String swaggerJson) {
        LOGGER.info("method is empty");
    }

    @Override
    public List<SwaggerResource> getSwaggerResource() {
        return SwaggerEFactory.createSwaggerE().getSwaggerResource();
    }

    @Override
    public UiConfiguration getUiConfiguration() {
        return SwaggerEFactory.createSwaggerE().getUiConfiguration();
    }

    @Override
    public SecurityConfiguration getSecurityConfiguration() {
        return SwaggerEFactory.createSwaggerE().getSecurityConfiguration();
    }
}
