package io.choerodon.manager.api.controller.v1;

import java.util.HashMap;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.base.domain.Sort;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.mybatis.annotation.SortDefault;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.manager.api.dto.InstanceDTO;
import io.choerodon.manager.api.dto.InstanceDetailDTO;
import io.choerodon.manager.app.service.InstanceService;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author flyleft
 */
@RestController
@RequestMapping(value = "/v1/instances")
public class InstanceController {

    private InstanceService instanceService;

    @Autowired
    public InstanceController(InstanceService instanceService) {
        this.instanceService = instanceService;
    }

    public void setInstanceService(InstanceService instanceService) {
        this.instanceService = instanceService;
    }

    /**
     * 查询实例列表
     *
     * @param service 服务名, 为空则查询所有
     * @return 实例列表
     */
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("查询实例列表")
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<PageInfo<InstanceDTO>> list(@RequestParam(value = "service", required = false) String service,
                                                      @ApiIgnore
                                                      @SortDefault(value = "instanceId", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                      @RequestParam(required = false, name = "instanceId") String instanceId,
                                                      @RequestParam(required = false, name = "version") String version,
                                                      @RequestParam(required = false, name = "status") String status,
                                                      @RequestParam(required = false, name = "params") String params) {
        Map<String, Object> map = new HashMap<>();
        map.put("instanceId", instanceId);
        map.put("service", service);
        map.put("version", version);
        map.put("status", status);
        map.put("params", params);
        return new ResponseEntity<>(instanceService.listByOptions(service, map, pageRequest), HttpStatus.OK);
    }

    /**
     * 查询实例详情
     *
     * @param instanceId 实例ID
     * @return 实例详情
     */
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("查询实例详情")
    @GetMapping(value = "/{instance_id:.*}")
    public InstanceDetailDTO query(@PathVariable("instance_id") String instanceId) {
        return instanceService.query(instanceId);
    }

    /**
     * 修改实例的配置
     *
     * @param instanceId 实例ID
     * @param configId   配置的id
     */
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("修改实例的配置")
    @PutMapping(value = "/{instance_id:.*}/configs/{config_id}")
    public void update(@PathVariable("instance_id") String instanceId,
                       @PathVariable("config_id") Long configId) {
        instanceService.update(instanceId, configId);
    }

}
