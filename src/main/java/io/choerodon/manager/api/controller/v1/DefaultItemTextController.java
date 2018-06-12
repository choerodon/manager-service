package io.choerodon.manager.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.manager.api.dto.ConfigDTO;
import io.choerodon.manager.api.validator.ConfigFileTypeValidator;
import io.choerodon.manager.app.service.ItemTextService;
import io.choerodon.manager.app.service.ConfigService;
import io.choerodon.swagger.annotation.Permission;

/**
 * 操作配置使用文本形式控制器
 *
 * @author wuguokai
 **/
@RequestMapping(value = "/v1/configs/{serviceName}/default/item/text")
@RestController
public class DefaultItemTextController {
    private ItemTextService itemTextService;
    private ConfigService configService;

    public DefaultItemTextController(ItemTextService itemTextService, ConfigService configService) {
        this.itemTextService = itemTextService;
        this.configService = configService;
    }

    /**
     * 以文本形式获取某个服务当前默认配置的内容，目前只支持properties与yaml
     *
     * @param serviceName 服务名
     * @param type        文本类型
     * @return String
     */
    @Permission(level = ResourceLevel.SITE, roles = {"managerAdmin"})
    @ApiOperation("以文本形式获取某个服务当前默认配置的内容，目前只支持properties与yaml")
    @GetMapping(value = "/{type}")
    public ResponseEntity<String> query(@PathVariable("serviceName") String serviceName,
                                        @PathVariable("type") String type) {
        ConfigDTO configDTO = configService.queryDefaultByServiceName(serviceName);
        ConfigFileTypeValidator.validate(type);
        return new ResponseEntity<>(itemTextService.getConfigText(configDTO.getId(), type), HttpStatus.OK);
    }

    /**
     * 以文本形式更新某个服务当前默认配置的内容，目前仅支持properties与yaml
     *
     * @param serviceName 服务名
     * @param type        文本类型
     * @param text        文本配置信息
     * @return null
     */
    @Permission(level = ResourceLevel.SITE, roles = {"managerAdmin"})
    @ApiOperation("以文本形式更新某个服务当前默认配置的内容，目前仅支持properties与yaml")
    @PatchMapping(value = "/{type}")
    public ResponseEntity update(@PathVariable("serviceName") String serviceName,
                                 @PathVariable("type") String type,
                                 @RequestBody String text) {
        ConfigFileTypeValidator.validate(type);
        ConfigDTO configDTO = configService.queryDefaultByServiceName(serviceName);
        itemTextService.updateConfigText(configDTO.getId(), type, text);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
