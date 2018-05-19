package io.choerodon.manager.api.controller.v1;

import java.util.Optional;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.manager.api.dto.ItemDto;
import io.choerodon.manager.api.dto.ServiceConfigDTO;
import io.choerodon.manager.app.service.ItemService;
import io.choerodon.manager.app.service.ServiceConfigService;
import io.choerodon.swagger.annotation.Permission;

/**
 * 操作服务默认配置项控制器
 *
 * @author wuguokai
 */
@RestController
@RequestMapping(value = "/v1/configs/{serviceName}/default/item")
public class DefaultItemController {
    private ItemService itemService;
    private ServiceConfigService serviceConfigService;

    public DefaultItemController(ItemService itemService, ServiceConfigService serviceConfigService) {
        this.itemService = itemService;
        this.serviceConfigService = serviceConfigService;
    }

    /**
     * 给某一服务的默认配置增加或更新配置项
     *
     * @param serviceName 服务名
     * @param item        条目对象
     * @return ItemDto
     */
    @Permission(level = ResourceLevel.SITE, roles = {"managerAdmin"})
    @ApiOperation("给某一服务的默认配置增加或更新配置项")
    @PatchMapping
    public ResponseEntity<ItemDto> add(@PathVariable("serviceName") String serviceName,
                                       @RequestBody ItemDto item) {
        ServiceConfigDTO serviceConfigDTO = serviceConfigService.queryDefaultByServiceName(serviceName);
        return Optional.ofNullable(itemService.saveItem(serviceConfigDTO.getId(), item))
                .map(i -> new ResponseEntity<>(i, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.config.item.add"));
    }

    /**
     * 给某一服务的默认配置删除配置项
     *
     * @param serviceName 服务名
     * @param property    配置key
     * @return null
     */
    @Permission(level = ResourceLevel.SITE, roles = {"managerAdmin"})
    @ApiOperation("给某一服务的默认配置删除配置项")
    @DeleteMapping
    public ResponseEntity delete(@PathVariable("serviceName") String serviceName,
                                 @RequestParam("property") String property) {
        ServiceConfigDTO serviceConfigDTO = serviceConfigService.queryDefaultByServiceName(serviceName);
        itemService.deleteItem(serviceConfigDTO.getId(), property);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
