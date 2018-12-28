package io.choerodon.manager.api.controller.v1;

import java.util.Optional;
import javax.validation.Valid;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.manager.api.dto.*;
import io.choerodon.manager.app.service.ConfigService;
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

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("创建配置")
    @PostMapping
    public ResponseEntity<ConfigDTO> create(@RequestBody @Valid CreateConfigDTO configDTO) {
        return new ResponseEntity<>(configService.create(configDTO), HttpStatus.OK);
    }

    /**
     * 删除配置，默认配置不可删除
     *
     * @param configId 配置id
     * @return ConfigDTO
     */
    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("删除配置，默认配置不可删除")
    @DeleteMapping(value = "/{config_id}")
    public ResponseEntity<Boolean> delete(@PathVariable("config_id") Long configId) {
        return new ResponseEntity<>(configService.delete(configId), HttpStatus.OK);
    }

    /**
     * 将某一个配置设置为默认配置
     *
     * @param configId 配置id
     * @return ConfigDTO
     */
    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("设置配置为默认配置")
    @PutMapping(value = "/{config_id}/default")
    public ResponseEntity<ConfigDTO> updateConfigDefault(@PathVariable("config_id") Long configId) {
        return new ResponseEntity<>(configService.setServiceConfigDefault(configId), HttpStatus.OK);
    }

    /**
     * 查询某一个配置
     *
     * @param configId 配置id
     * @return ConfigDTO
     */
    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("查询配置")
    @GetMapping(value = "/{config_id}")
    public ResponseEntity<ConfigDTO> query(@PathVariable("config_id") Long configId,
                                           @ApiParam("要返回的配置文本类型，可为properties或者yaml，对应DTO的txt字段，为空不返回")
                                           @RequestParam(value = "type", required = false) String type) {
        return new ResponseEntity<>(configService.query(configId, type), HttpStatus.OK);
    }

    /**
     * 查询配置的yaml形式
     *
     * @param configId 配置id
     * @return 查询配置的yaml形式
     */
    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("查询配置的yaml形式")
    @GetMapping(value = "/{config_id}/yaml")
    public ResponseEntity<YamlDTO> queryYaml(@PathVariable("config_id") Long configId) {
        return new ResponseEntity<>(configService.queryYaml(configId), HttpStatus.OK);
    }


    /**
     * 给配置增加或修改配置项
     *
     * @param configId 配置id
     * @param item     配置项对象
     * @return ItemDto
     */
    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("增加或修改配置项")
    @PostMapping("/{config_id}/items")
    public ResponseEntity<ItemDto> addItem(@PathVariable("config_id") Long configId,
                                           @RequestBody ItemDto item) {
        return Optional.ofNullable(configService.saveItem(configId, item))
                .map(i -> new ResponseEntity<>(i, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.config.item.add"));
    }

    /**
     * 删除配置的一个配置项
     *
     * @param configId 配置id
     * @param property 配置项key
     * @return null
     */
    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("删除配置项")
    @DeleteMapping("/{config_id}/items")
    public ResponseEntity deleteItem(@PathVariable("config_id") Long configId,
                                     @RequestParam("property") String property) {
        configService.deleteItem(configId, property);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * 修改配置
     */
    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("修改配置")
    @PutMapping("/{config_id}")
    public ResponseEntity<ConfigDTO> updateConfig(@PathVariable("config_id") Long configId,
                                                  @ApiParam("要更新的配置文本类型，可为properties或者yaml，对应DTO的txt字段，为空则根据value值更新")
                                                  @RequestParam(value = "type", required = false) String type,
                                                  @RequestBody ConfigDTO configDTO) {
        configDTO.setId(configId);
        return new ResponseEntity<>(configService.updateConfig(configId, configDTO, type), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation(value = "配置校验接口")
    @PostMapping(value = "/check")
    public ResponseEntity check(@RequestBody ConfigCheckDTO configDTO) {
        configService.check(configDTO);
        return new ResponseEntity(HttpStatus.OK);
    }
}
