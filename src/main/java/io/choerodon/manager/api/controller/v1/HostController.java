package io.choerodon.manager.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.manager.api.dto.HostDTO;
import io.choerodon.manager.api.dto.HostVO;
import io.choerodon.manager.api.dto.ServiceVO;
import io.choerodon.manager.app.service.HostService;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author wanghao
 * @Date 2019/11/1 17:50
 */
@RestController
@RequestMapping("/v1/hosts")
public class HostController {

    private HostService hostService;

    public HostController(HostService hostService) {
        this.hostService = hostService;
    }

    @GetMapping
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("分页查询所有主机")
    public ResponseEntity<PageInfo<ServiceVO>> pagingHosts(
            @RequestParam(name = "source_type", required = false) String sourceType,
            @RequestParam(name = "host_name", required = false) String hostName,
            @RequestParam(name = "ip_addr", required = false) String ipAddr,
            @RequestParam(name = "port", required = false) Integer port,
            @RequestParam(name = "app_name", required = false) String appName,
            @ApiIgnore
            @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(hostService.pagingHosts(sourceType, hostName, ipAddr, port, appName, pageable));
    }

    @DeleteMapping("/{app_name}/{instance_id}")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("删除主机")
    public ResponseEntity<Void> deleteHost(
            @PathVariable(name = "app_name") String appName,
            @PathVariable(name = "instance_id") String instanceId
    ) {
        hostService.deleteHost(appName, instanceId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{app_name}")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("添加主机")
    public ResponseEntity<Void> saveHost(
            @PathVariable(name = "app_name") String appName,
            @RequestBody @Validated HostVO hostVO
    ) {
        hostService.saveHost(appName, hostVO);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/search")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("根据服务名/主机名/IP查询主机信息")
    public ResponseEntity<List<HostDTO>> listHosts(
            @RequestParam(name = "param", required = false) String param
    ) {
        return ResponseEntity.ok(hostService.listHosts(param));
    }
}
