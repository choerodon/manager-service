package io.choerodon.manager.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.manager.api.dto.swagger.ControllerDTO;
import io.choerodon.manager.app.service.ApiService;
import io.choerodon.manager.app.service.SwaggerService;
import io.choerodon.manager.infra.common.utils.VersionUtil;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.swagger.web.SwaggerResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @CustomPageRequest
    public ResponseEntity<Page<ControllerDTO>> queryByNameAndVersion(
            @PathVariable("service_prefix") String serviceName,
            @RequestParam(value = "version", required = false,defaultValue = VersionUtil.NULL_VERSION) String version,
            @RequestParam(required = false, name = "params") String params,
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "description") String description,
            @SortDefault(value = "name", direction = Sort.Direction.ASC)
                    PageRequest pageRequest) {
        Map<String, Object> map = new HashMap<>();
        map.put("params", params);
        map.put("name", name);
        map.put("description", description);
        return new ResponseEntity<>(apiService.getControllers(serviceName, version, pageRequest, map), HttpStatus.OK);
    }
}
