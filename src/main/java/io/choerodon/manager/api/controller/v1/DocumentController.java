package io.choerodon.manager.api.controller.v1;


import io.choerodon.manager.app.service.ApiService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.manager.infra.common.utils.VersionUtil;
import io.choerodon.swagger.annotation.Permission;

/**
 * 获取swagger信息controller
 *
 * @author flyleft
 * @author wuguokai
 * @author superleader8@gmail.com
 */
@RestController
@RequestMapping(value = "/docs")
public class DocumentController {

    private ApiService apiService;

    /**
     * 构造器
     */
    public DocumentController(ApiService apiService) {
        this.apiService = apiService;
    }

    /**
     * 获取服务id对应的版本的swagger json
     *
     * @param name    服务id，形如 uaa
     * @param version 服务版本
     * @return String
     */
    @Permission(permissionPublic = true, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("获取服务id对应的版本swagger json字符串")
    @GetMapping(value = "/{service_prefix}")
    public ResponseEntity<String> get(@PathVariable("service_prefix") String name,
                                      @RequestParam(value = "version", required = false,
                                              defaultValue = VersionUtil.NULL_VERSION) String version) {
        String swaggerJson = apiService.getSwaggerJson(name, version);
        if (StringUtils.isEmpty(swaggerJson)) {
            return new ResponseEntity<>(swaggerJson, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(swaggerJson, HttpStatus.OK);
        }
    }
}
