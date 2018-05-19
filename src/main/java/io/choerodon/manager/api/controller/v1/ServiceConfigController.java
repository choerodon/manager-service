package io.choerodon.manager.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.manager.api.dto.ServiceConfigDTO;
import io.choerodon.manager.app.service.ServiceConfigService;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;

/**
 * 操作config表的controller
 *
 * @author wuguokai
 */
@RestController
@RequestMapping(value = "/v1/configs")
public class ServiceConfigController {
    private ServiceConfigService serviceConfigService;

    public ServiceConfigController(ServiceConfigService serviceConfigService) {
        this.serviceConfigService = serviceConfigService;
    }

    /**
     * 由config-server调用
     * 通过服务名获取配置信息，对外隐藏api
     *
     * @param serviceName 服务名
     * @return ServiceConfigDTO
     */
    @ApiIgnore
    @GetMapping("/{service_name}/default")
    public ResponseEntity<ServiceConfigDTO> queryByServiceName(@PathVariable("service_name") String serviceName) {
        return new ResponseEntity<>(serviceConfigService.queryDefaultByServiceName(serviceName), HttpStatus.OK);
    }

    /**
     * 由config-server调用
     * 通过服务名和配置版本获取配置信息，对外隐藏api
     *
     * @param serviceName   服务名
     * @param configVersion 配置版本
     * @return ServiceConfigDTO
     */
    @ApiIgnore
    @GetMapping("/{service_name}/by_version")
    public ResponseEntity<ServiceConfigDTO> queryByServiceNameAndConfigVersion(@PathVariable("service_name") String serviceName,
                                                                               @RequestParam("config_version") String configVersion) {
        return new ResponseEntity<>(serviceConfigService.queryByServiceNameAndConfigVersion(serviceName, configVersion),
                HttpStatus.OK);
    }


    /**
     * 分页查询服务的配置信息
     *
     * @param serviceId 服务id，可为空，为空则查询所有服务的服务信息
     * @return Page
     */
    @Permission(level = ResourceLevel.SITE, roles = {"managerAdmin"})
    @ApiOperation("分页查询服务的配置信息，服务id为空则查询所有服务的服务信息")
    @GetMapping
    public ResponseEntity<Page<ServiceConfigDTO>> list(
            @RequestParam(value = "serviceId", required = false) Long serviceId,
            @SortDefault(value = "id", direction = Sort.Direction.ASC) PageRequest pageRequest) {
        if (StringUtils.isEmpty(serviceId)) {
            return new ResponseEntity<>(serviceConfigService.list(pageRequest), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(serviceConfigService.listByServiceId(serviceId, pageRequest), HttpStatus.OK);
        }

    }

    /**
     * 查询某一个配置
     *
     * @param configId 配置id
     * @return ServiceConfigDTO
     */
    @Permission(level = ResourceLevel.SITE, roles = {"managerAdmin"})
    @ApiOperation("查询某一个配置")
    @GetMapping(value = "/{config_id}")
    public ResponseEntity<ServiceConfigDTO> query(@PathVariable("config_id") Long configId) {
        return new ResponseEntity<>(serviceConfigService.query(configId), HttpStatus.OK);
    }


    /**
     * 将某一个配置设置为默认配置
     *
     * @param configId 配置id
     * @return ServiceConfigDTO
     */
    @Permission(level = ResourceLevel.SITE, roles = {"managerAdmin"})
    @ApiOperation("将某一个配置设置为默认配置")
    @PutMapping(value = "/{config_id}/default")
    public ResponseEntity<ServiceConfigDTO> updateConfigDefault(@PathVariable("config_id") Long configId) {
        return new ResponseEntity<>(serviceConfigService.setServiceConfigDefault(configId), HttpStatus.OK);
    }

    /**
     * 删除某一个配置
     *
     * @param configId 配置id
     * @return ServiceConfigDTO
     */
    @Permission(level = ResourceLevel.SITE, roles = {"managerAdmin"})
    @ApiOperation("删除某一个配置")
    @DeleteMapping(value = "/{config_id}")
    public ResponseEntity<Boolean> delete(@PathVariable("config_id") Long configId) {
        return new ResponseEntity<>(serviceConfigService.delete(configId), HttpStatus.OK);
    }
}
