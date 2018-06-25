package io.choerodon.manager.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.manager.api.dto.InstanceDetailDTO;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.manager.api.dto.InstanceDTO;
import io.choerodon.manager.app.service.InstanceService;
import io.choerodon.swagger.annotation.Permission;
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

    /**
     * 查询实例列表
     *
     * @param service 服务名, 为空则查询所有
     * @return 实例列表
     */
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("查询实例列表")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<Page<InstanceDTO>> list(@RequestParam(value = "service", required = false) String service,
                                  @ApiIgnore
                                  @SortDefault(value = "id", direction = Sort.Direction.DESC)
                                          PageRequest pageRequest,
                                  @RequestParam(required = false, name = "instanceId") String instanceId,
                                  @RequestParam(required = false, name = "version") String version,
                                  @RequestParam(required = false, name = "status") String status,
                                  @RequestParam(required = false, name = "params") String params) {
        return new ResponseEntity<>(instanceService.listByOptions(new InstanceDTO(instanceId,
                service, version, status, params, null, null), pageRequest), HttpStatus.OK);
    }

    /**
     * 查询实例详情
     *
     * @param instanceId 实例ID
     * @return 实例详情
     */
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("查询实例详情")
    @GetMapping(value = "/{instance_id:.*}")
    public InstanceDetailDTO query(@PathVariable("instance_id") String instanceId) {
        return instanceService.query(instanceId);
    }

    /**
     * 修改实例的配置
     *
     * @param instanceId    实例ID
     * @param configId 配置的id
     */
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("修改实例的配置")
    @PutMapping(value = "/{instance_id:.*}/configs/{config_id}")
    public void update(@PathVariable("instance_id") String instanceId,
                       @PathVariable("config_id") Long configId) {
        instanceService.update(instanceId, configId);
    }

}
