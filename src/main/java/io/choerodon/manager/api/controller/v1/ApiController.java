package io.choerodon.manager.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.manager.api.dto.swagger.ControllerDTO;
import io.choerodon.manager.app.service.ApiService;
import io.choerodon.manager.app.service.SwaggerService;
import io.choerodon.manager.infra.common.utils.VersionUtil;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.swagger.web.SwaggerResource;

import java.util.List;

/**
 * @author superlee
 */
@RestController
@RequestMapping(value = "/v1/swaggers")
public class ApiController {

    private SwaggerService swaggerService;

    private ApiService apiService;

    public ApiController(SwaggerService swaggerService, ApiService apiService) {
        this.swaggerService = swaggerService;
        this.apiService = apiService;
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("查询不包含跳过的服务的路由列表")
    @GetMapping("/resources")
    public ResponseEntity<List<SwaggerResource>> resources() {
        return new ResponseEntity<>(swaggerService.getSwaggerResource(), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("查询服务controller和接口")
    @GetMapping("/controllers/{service_prefix}")
    public ResponseEntity<List<ControllerDTO>> queryByNameAndVersion(
            @PathVariable("service_prefix") String name,
            @RequestParam(value = "version", required = false,defaultValue = VersionUtil.NULL_VERSION) String version) {
        return new ResponseEntity<>(apiService.getControllers(name, version), HttpStatus.OK);
    }



}
