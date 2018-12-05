package io.choerodon.manager.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.UiConfiguration;

import io.choerodon.manager.app.service.SwaggerService;

/**
 * swagger控制器
 *
 * @author huiyu.chen
 * @author wuguokai
 */
@RestController
@Api(description = "swagger")
public class SwaggerController {

    private SwaggerService swaggerService;

    public SwaggerController(SwaggerService swaggerService) {
        this.swaggerService = swaggerService;
    }

    @ApiIgnore
    @RequestMapping(value = "/swagger-resources/configuration/security")
    ResponseEntity<SecurityConfiguration> securityConfiguration() {
        return new ResponseEntity<>(swaggerService.getSecurityConfiguration(), HttpStatus.OK);
    }

    @ApiIgnore
    @RequestMapping(value = "/swagger-resources/configuration/ui")
    ResponseEntity<UiConfiguration> uiConfiguration() {
        return new ResponseEntity<>(swaggerService.getUiConfiguration(), HttpStatus.OK);
    }

    /**
     * 获取swagger服务列表，swagger页面自动请求
     *
     * @return list
     */
    @ApiIgnore
    @RequestMapping(value = "/swagger-resources")
    ResponseEntity<List<SwaggerResource>> swaggerResources() {
        return new ResponseEntity<>(swaggerService.getSwaggerResource(), HttpStatus.OK);
    }
}
