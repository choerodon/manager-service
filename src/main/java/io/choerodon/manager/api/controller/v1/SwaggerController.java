package io.choerodon.manager.api.controller.v1;

import io.choerodon.manager.app.service.SwaggerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.UiConfiguration;

import java.util.List;

/**
 * swagger控制器
 *
 * @author huiyu.chen
 * @author wuguokai
 */
@RestController
public class SwaggerController {

    private SwaggerService swaggerService;

    public SwaggerController(SwaggerService swaggerService) {
        this.swaggerService = swaggerService;
    }

    @ApiIgnore
    @GetMapping(value = "/swagger-resources/configuration/security")
    public ResponseEntity<SecurityConfiguration> securityConfiguration() {
        return new ResponseEntity<>(swaggerService.getSecurityConfiguration(), HttpStatus.OK);
    }

    @ApiIgnore
    @GetMapping(value = "/swagger-resources/configuration/ui")
    public ResponseEntity<UiConfiguration> uiConfiguration() {
        return new ResponseEntity<>(swaggerService.getUiConfiguration(), HttpStatus.OK);
    }

    /**
     * 获取swagger服务列表，swagger页面自动请求
     *
     * @return list
     */
    @ApiIgnore
    @GetMapping(value = "/swagger-resources")
    public ResponseEntity<List<SwaggerResource>> swaggerResources() {
        return new ResponseEntity<>(swaggerService.getSwaggerResource(), HttpStatus.OK);
    }
}
