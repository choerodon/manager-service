package io.choerodon.manager.api.controller.v1;

import java.io.IOException;

import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.web.bind.annotation.*;

import io.choerodon.manager.app.service.DocumentService;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);

    private DocumentService documentService;

    /**
     * 构造器
     */
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * 获取服务id对应的版本的swagger json
     *
     * @param name    服务id，形如 uaa
     * @param version 服务版本
     * @return String
     */
    @Permission(permissionPublic = true)
    @ApiOperation("获取服务id对应的版本swagger json字符串")
    @GetMapping(value = "/{service_prefix}")
    public ResponseEntity<String> get(@PathVariable("service_prefix") String name,
                                      @RequestParam(value = "version", required = false,
                                              defaultValue = VersionUtil.NULL_VERSION) String version) {
        String swaggerJson;
        try {
            swaggerJson = documentService.getSwaggerJson(name, version);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            String log = "服务" + name + " version " + version + "没有在运行";
            return new ResponseEntity<>(log, HttpStatus.NOT_FOUND);
        }
        if ("".equals(swaggerJson)) {
            return new ResponseEntity<>(swaggerJson, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(swaggerJson, HttpStatus.OK);
        }
    }

    /**
     * 手动刷新表中swagger和刷新权限
     *
     * @param serviceName 服务名
     * @param version     服务版本
     * @return null
     */
    @ApiOperation("手动刷新表中swagger json和权限")
    @PutMapping(value = "/permission/refresh/{service_name}")
    public ResponseEntity refresh(@PathVariable("service_name") String serviceName,
                                  @RequestParam(value = "version", required = false, defaultValue = VersionUtil.NULL_VERSION) String version) {
        try {
            documentService.manualRefresh(serviceName, version);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RemoteAccessException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
