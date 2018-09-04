package io.choerodon.manager.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.web.bind.annotation.*;

import io.choerodon.manager.app.service.DocumentService;
import io.choerodon.manager.infra.common.utils.VersionUtil;

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
