package io.choerodon.manager.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.manager.api.dto.ConfigDTO;
import io.choerodon.manager.api.dto.InstanceDTO;
import io.choerodon.manager.api.dto.ServiceDTO;
import io.choerodon.manager.app.service.ConfigService;
import io.choerodon.manager.app.service.ServiceService;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Optional;

/**
 * 操作服务控制器
 *
 * @author wuguokai
 */
@RestController
@RequestMapping(value = "/v1/services")
public class ServiceController {

    private ServiceService serviceService;
    private ConfigService configService;

    public ServiceController(ServiceService serviceService, ConfigService configService) {
        this.serviceService = serviceService;
        this.configService = configService;
    }

    /**
     * 分页查询服务信息
     *
     * @return page
     */
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("分页查询服务信息")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<Page<ServiceDTO>> pageAll(
            @ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.ASC) PageRequest pageRequest) {
        return Optional.ofNullable(serviceService.pageAll(pageRequest))
                .map(i -> new ResponseEntity<>(i, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.service.query"));
    }

    /**
     * 查询服务实例列表
     *
     * @param service 服务名
     * @return 实例列表
     */
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("查询服务实例列表")
    @CustomPageRequest
    @GetMapping(value = "/{service_name}/instances")
    public ResponseEntity<Page<InstanceDTO>> listByServiceName(@PathVariable("service_name") String service,
                                                               @ApiIgnore
                                                               @SortDefault(value = "id", direction = Sort.Direction.DESC)
                                                                       PageRequest pageRequest,
                                                               @RequestParam(required = false, name = "instanceId") String instanceId,
                                                               @RequestParam(required = false, name = "version") String version,
                                                               @RequestParam(required = false, name = "status") String status,
                                                               @RequestParam(required = false, name = "params") String params) {
        return new ResponseEntity<>(serviceService.listByServiceName(new InstanceDTO(instanceId,
                service, version, status, params), pageRequest), HttpStatus.OK);
    }

    /**
     * 内部接口，由config-server调用
     * 通过服务名获取配置信息，对外隐藏api
     *
     * @param serviceName 服务名
     * @return ConfigDTO
     */
    @GetMapping("/{service_name}/configs/default")
    @ApiIgnore
    public ResponseEntity<ConfigDTO> queryDefaultConfigByServiceName(@PathVariable("service_name") String serviceName) {
        return new ResponseEntity<>(configService.queryDefaultByServiceName(serviceName), HttpStatus.OK);
    }

    /**
     * 内部接口，由config-server调用
     * 通过服务名和配置版本获取配置信息，对外隐藏api
     *
     * @param serviceName   服务名
     * @param configVersion 配置版本
     * @return ConfigDTO
     */
    @ApiIgnore
    @GetMapping("/{service_name}/configs/{config_version:.*}")
    public ResponseEntity<ConfigDTO> queryConfigByServiceNameAndVersion(@PathVariable("service_name") String serviceName,
                                                                        @PathVariable("config_version") String configVersion) {
        return new ResponseEntity<>(configService.queryByServiceNameAndConfigVersion(serviceName, configVersion),
                HttpStatus.OK);
    }

    /**
     * 分页查询服务的配置信息
     *
     * @param serviceName 服务id，可为空，为空则查询所有服务的服务信息
     * @return Page
     */
    @Permission(level = ResourceLevel.SITE)
    @CustomPageRequest
    @ApiOperation("分页模糊查询服务的配置")
    @GetMapping("/{service_name}/configs")
    public ResponseEntity<Page<ConfigDTO>> list(
            @PathVariable("service_name") String serviceName,
            @ApiIgnore
            @SortDefault(value = "id", direction = Sort.Direction.DESC)
                    PageRequest pageRequest,
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "source") String source,
            @RequestParam(required = false, name = "configVersion") String configVersion,
            @RequestParam(required = false, name = "isDefault") Boolean isDefault,
            @RequestParam(required = false, name = "params") String param) {
        return new ResponseEntity<>(configService.listByServiceName(serviceName, pageRequest,
                new ConfigDTO(name, configVersion, isDefault, source), param), HttpStatus.OK);
    }

}
