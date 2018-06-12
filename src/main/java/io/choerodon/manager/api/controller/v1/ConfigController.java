package io.choerodon.manager.api.controller.v1;

import io.choerodon.manager.api.dto.ConfigDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.manager.app.service.ConfigService;
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
public class ConfigController {
    private ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * 由config-server调用
     * 通过服务名获取配置信息，对外隐藏api
     *
     * @param serviceName 服务名
     * @return ConfigDTO
     */
    @ApiIgnore
    @GetMapping("/{service_name}/default")
    public ResponseEntity<ConfigDTO> queryByServiceName(@PathVariable("service_name") String serviceName) {
        return new ResponseEntity<>(configService.queryDefaultByServiceName(serviceName), HttpStatus.OK);
    }

    /**
     * 由config-server调用
     * 通过服务名和配置版本获取配置信息，对外隐藏api
     *
     * @param serviceName   服务名
     * @param configVersion 配置版本
     * @return ConfigDTO
     */
    @ApiIgnore
    @GetMapping("/{service_name}/by_version")
    public ResponseEntity<ConfigDTO> queryByServiceNameAndConfigVersion(@PathVariable("service_name") String serviceName,
                                                                        @RequestParam("config_version") String configVersion) {
        return new ResponseEntity<>(configService.queryByServiceNameAndConfigVersion(serviceName, configVersion),
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
    public ResponseEntity<Page<ConfigDTO>> list(
            @RequestParam(value = "serviceId", required = false) Long serviceId,
            @SortDefault(value = "id", direction = Sort.Direction.ASC) PageRequest pageRequest) {
        if (StringUtils.isEmpty(serviceId)) {
            return new ResponseEntity<>(configService.list(pageRequest), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(configService.listByServiceId(serviceId, pageRequest), HttpStatus.OK);
        }

    }

    /**
     * 查询某一个配置
     *
     * @param configId 配置id
     * @return ConfigDTO
     */
    @Permission(level = ResourceLevel.SITE, roles = {"managerAdmin"})
    @ApiOperation("查询某一个配置")
    @GetMapping(value = "/{config_id}")
    public ResponseEntity<ConfigDTO> query(@PathVariable("config_id") Long configId) {
        return new ResponseEntity<>(configService.query(configId), HttpStatus.OK);
    }


    /**
     * 将某一个配置设置为默认配置
     *
     * @param configId 配置id
     * @return ConfigDTO
     */
    @Permission(level = ResourceLevel.SITE, roles = {"managerAdmin"})
    @ApiOperation("将某一个配置设置为默认配置")
    @PutMapping(value = "/{config_id}/default")
    public ResponseEntity<ConfigDTO> updateConfigDefault(@PathVariable("config_id") Long configId) {
        return new ResponseEntity<>(configService.setServiceConfigDefault(configId), HttpStatus.OK);
    }

    /**
     * 删除某一个配置
     *
     * @param configId 配置id
     * @return ConfigDTO
     */
    @Permission(level = ResourceLevel.SITE, roles = {"managerAdmin"})
    @ApiOperation("删除某一个配置")
    @DeleteMapping(value = "/{config_id}")
    public ResponseEntity<Boolean> delete(@PathVariable("config_id") Long configId) {
        return new ResponseEntity<>(configService.delete(configId), HttpStatus.OK);
    }
}
