package io.choerodon.manager.app.service;

import java.util.List;

import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.UiConfiguration;

/**
 * @author superleader8@gmail.com
 * @data 2018/3/15
 */
public interface SwaggerService {

    List<SwaggerResource> getSwaggerResource();

    UiConfiguration getUiConfiguration();

    SecurityConfiguration getSecurityConfiguration();
}
