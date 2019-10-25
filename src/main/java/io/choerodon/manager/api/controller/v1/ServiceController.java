package io.choerodon.manager.api.controller.v1;

import java.util.List;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.manager.api.dto.ConfigVO;
import io.choerodon.manager.infra.dto.ConfigDTO;
import io.choerodon.manager.infra.dto.ServiceDTO;
import org.springframework.data.web.SortDefault;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.manager.api.dto.ServiceManagerDTO;
import io.choerodon.manager.app.service.ConfigService;
import io.choerodon.manager.app.service.ServiceService;

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

    public void setServiceService(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public ServiceController(ServiceService serviceService, ConfigService configService) {
        this.serviceService = serviceService;
        this.configService = configService;
    }

    /**
     * 查询服务列表
     *
     * @return page
     */
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("微服务管理列表")
    @CustomPageRequest
    @GetMapping("/manager")
    public ResponseEntity<PageInfo<ServiceManagerDTO>> pageManager(
            @RequestParam(required = false, name = "service_name") String serviceName,
            @RequestParam(required = false) String params,
            @ApiIgnore
            @SortDefault(value = "name", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(serviceService.pageManager(serviceName, params, pageable), HttpStatus.OK);
    }

    /**
     * 查询服务列表
     *
     * @return page
     */
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_DEVELOPER}, permissionLogin = true)
    @ApiOperation("查询服务列表")
    @GetMapping
    public ResponseEntity<List<ServiceDTO>> pageAll(@RequestParam(required = false) String param) {
        return new ResponseEntity<>(serviceService.list(param), HttpStatus.OK);
    }


    /**
     * 内部接口，由config-server调用
     * 通过服务名获取配置信息，对外隐藏api
     *
     * @param serviceName 服务名
     * @return ConfigVO
     */
    @Permission(permissionWithin = true)
    @GetMapping("/{service_name}/configs/default")
    @ApiIgnore
    public ResponseEntity<ConfigVO> queryDefaultConfigByServiceName(@PathVariable("service_name") String serviceName) {
        return new ResponseEntity<>(configService.queryDefaultByServiceName(serviceName), HttpStatus.OK);
    }

    /**
     * 内部接口，由config-server调用
     * 通过服务名和配置版本获取配置信息，对外隐藏api
     *
     * @param serviceName   服务名
     * @param configVersion 配置版本
     * @return ConfigVO
     */
    @Permission(permissionWithin = true)
    @ApiIgnore
    @GetMapping("/{service_name}/configs/{config_version:.*}")
    public ResponseEntity<ConfigVO> queryConfigByServiceNameAndVersion(@PathVariable("service_name") String serviceName,
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
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("分页模糊查询服务的配置")
    @GetMapping("/{service_name}/configs")
    @CustomPageRequest
    public ResponseEntity<PageInfo<ConfigVO>> list(
            @PathVariable("service_name") String serviceName,
            @ApiIgnore
            @SortDefault(value = "name", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "source") String source,
            @RequestParam(required = false, name = "configVersion") String configVersion,
            @RequestParam(required = false, name = "isDefault") Boolean isDefault,
            @RequestParam(required = false, name = "params") String param) {
        ConfigDTO dto = new ConfigDTO();
        dto.setName(name);
        dto.setConfigVersion(configVersion);
        dto.setIsDefault(isDefault);
        dto.setSource(source);
        return new ResponseEntity<>(configService.listByServiceName(serviceName, pageable, dto, param), HttpStatus.OK);
    }

}
