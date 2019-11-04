package io.choerodon.manager.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.manager.api.dto.HostDTO;
import io.choerodon.manager.app.service.HostService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wanghao
 * @Date 2019/11/1 17:50
 */
@RestController
@RequestMapping("/v1/host")
public class HostController {

    private HostService hostService;

    public HostController(HostService hostService) {
        this.hostService = hostService;
    }

    @GetMapping
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("分页查询所有主机")
    public ResponseEntity<PageInfo<HostDTO>> pagingHosts(
            @RequestParam(name = "sourceType",required = false) String sourceType,
            @RequestParam(name = "hostName",required = false) String hostName,
            @RequestParam(name = "ipAddr",required = false) String ipAddr,
            @RequestParam(name = "port",required = false) int port,
            @RequestParam(name = "appName",required = false) String appName,
            @RequestParam(name = "params",required = false) String[] params
    ) {
        return ResponseEntity.ok(hostService.pagingHosts(sourceType,hostName,ipAddr,port,appName,params));
    }
}
