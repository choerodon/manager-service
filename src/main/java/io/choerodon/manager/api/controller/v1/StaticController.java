package io.choerodon.manager.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.manager.api.dto.MenuClickDTO;
import io.choerodon.manager.app.service.StaticService;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author superlee
 */
@RestController
@RequestMapping(value = "/v1/static")
public class StaticController {

    private StaticService staticService;

    public StaticController(StaticService staticService) {
        this.staticService = staticService;
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("菜单点击次数统计保存接口")
    @PostMapping("/menu_click/save")
    public ResponseEntity saveMenuClick(@RequestBody List<MenuClickDTO> menuClickList) {
        staticService.saveMenuClick(menuClickList);
        return new ResponseEntity(HttpStatus.OK);
    }

}
