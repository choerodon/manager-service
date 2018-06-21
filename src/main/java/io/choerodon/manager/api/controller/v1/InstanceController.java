package io.choerodon.manager.api.controller.v1;

import java.util.List;

import com.netflix.appinfo.InstanceInfo;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.manager.api.dto.InstanceDTO;
import io.choerodon.manager.app.service.InstanceService;
import io.choerodon.swagger.annotation.Permission;

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
    public List<InstanceDTO> list(@RequestParam(value = "service", required = false) String service) {
        return instanceService.list(service);
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
    public InstanceInfo query(@PathVariable("instance_id") String instanceId) {
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
