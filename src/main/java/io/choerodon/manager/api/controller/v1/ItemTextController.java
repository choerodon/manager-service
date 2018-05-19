package io.choerodon.manager.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.manager.api.validator.ConfigFileTypeValidator;
import io.choerodon.manager.app.service.ItemTextService;
import io.choerodon.swagger.annotation.Permission;

/**
 * 根据配置id修改文本形式的配置
 *
 * @author wuguokai
 */
@RequestMapping(value = "/v1/configs/{configId}/item/text")
@RestController
public class ItemTextController {
    private ItemTextService itemTextService;

    public ItemTextController(ItemTextService itemTextService) {
        this.itemTextService = itemTextService;
    }

    /**
     * 以文本形式获取配置内容，目前只支持properties与yaml
     *
     * @param configId 配置id
     * @param type     文本配置类型
     * @return String
     */
    @Permission(level = ResourceLevel.SITE, roles = {"managerAdmin"})
    @ApiOperation("以文本形式获取配置内容，目前只支持properties与yaml")
    @GetMapping(value = "/{type}")
    public ResponseEntity<String> query(@PathVariable("configId") Long configId,
                                        @PathVariable("type") String type) {
        ConfigFileTypeValidator.validate(type);
        return new ResponseEntity<>(itemTextService.getConfigText(configId, type), HttpStatus.OK);
    }

    /**
     * 以文本形式更新配置内容，目前仅支持properties与yaml
     *
     * @param configId 配置id
     * @param type     文本配置类型
     * @param text     文本配置内容
     * @return null
     */
    @Permission(level = ResourceLevel.SITE, roles = {"managerAdmin"})
    @ApiOperation("以文本形式更新配置内容，目前仅支持properties与yaml")
    @PatchMapping(value = "/{type}")
    public ResponseEntity update(@PathVariable("configId") Long configId,
                                 @PathVariable("type") String type,
                                 @RequestBody String text) {
        ConfigFileTypeValidator.validate(type);
        itemTextService.updateConfigText(configId, type, text);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
