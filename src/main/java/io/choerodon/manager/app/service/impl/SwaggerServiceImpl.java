package io.choerodon.manager.app.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.UiConfiguration;

import io.choerodon.manager.app.service.SwaggerService;
import io.choerodon.manager.domain.service.ISwaggerService;

/**
 * @author superleader8@gmail.com
 * @data 2018/3/15
 */
@Component
public class SwaggerServiceImpl implements SwaggerService {

    private ISwaggerService service;

    public SwaggerServiceImpl(ISwaggerService service) {
        this.service = service;
    }

    @Override
    public List<SwaggerResource> getSwaggerResource() {
        return service.getSwaggerResource();
    }

    @Override
    public UiConfiguration getUiConfiguration() {
        return service.getUiConfiguration();
    }

    @Override
    public SecurityConfiguration getSecurityConfiguration() {
        return service.getSecurityConfiguration();
    }
}
