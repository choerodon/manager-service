package io.choerodon.manager.api.controller.v1;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.manager.api.dto.MenuClickDTO;
import io.choerodon.manager.app.service.StatisticService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author superlee
 */
@RestController
@RequestMapping(value = "/v1/statistic")
public class StatisticController {

    private StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @ApiOperation("菜单点击次数统计保存接口")
    @PostMapping("/menu_click/save")
    public ResponseEntity saveMenuClick(@RequestBody List<MenuClickDTO> menuClickList) {
        statisticService.saveMenuClick(menuClickList);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE)
    @ApiOperation("根据日期和层级查询菜单的调用次数")
    @GetMapping("/menu_click")
    public ResponseEntity<Map<String, Object>> queryMenuClick(@RequestParam(value = "begin_date")
                                                  @ApiParam(value = "日期格式yyyy-MM-dd", required = true) String beginDate,
                                              @RequestParam(value = "end_date")
                                                  @ApiParam(value = "日期格式yyyy-MM-dd", required = true) String endDate,
                                              @RequestParam String code) {
        return new ResponseEntity(statisticService.queryMenuClick(beginDate, endDate, code), HttpStatus.OK);
    }

}
