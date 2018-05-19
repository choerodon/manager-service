package io.choerodon.manager.api.controller.v1;

import java.util.Optional;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.manager.api.dto.ItemDto;
import io.choerodon.manager.app.service.ItemService;
import io.choerodon.swagger.annotation.Permission;

/**
 * 根据配置id操作配置项控制器
 *
 * @author wuguokai
 */
@RestController
@RequestMapping(value = "/v1/configs/{configId}/item")
public class ItemController {
    private ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * 给某一配置增加或更新配置项
     *
     * @param configId 配置id
     * @param item     配置项对象
     * @return ItemDto
     */
    @Permission(level = ResourceLevel.SITE, roles = {"managerAdmin"})
    @ApiOperation("给某一配置增加或更新配置项")
    @PatchMapping
    public ResponseEntity<ItemDto> add(@PathVariable("configId") Long configId,
                                       @RequestBody ItemDto item) {
        return Optional.ofNullable(itemService.saveItem(configId, item))
                .map(i -> new ResponseEntity<>(i, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.config.item.add"));
    }

    /**
     * 删除某一配置的一个配置项
     *
     * @param configId 配置id
     * @param property 配置项key
     * @return null
     */
    @Permission(level = ResourceLevel.SITE, roles = {"managerAdmin"})
    @ApiOperation("删除某一配置的一个配置项")
    @DeleteMapping
    public ResponseEntity delete(@PathVariable("configId") Long configId,
                                 @RequestParam("property") String property) {
        itemService.deleteItem(configId, property);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
