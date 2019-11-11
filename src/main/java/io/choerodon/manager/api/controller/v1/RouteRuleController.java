package io.choerodon.manager.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.manager.api.dto.RouteRuleVO;
import io.choerodon.manager.api.validator.Insert;
import io.choerodon.manager.api.validator.Update;
import io.choerodon.manager.app.service.RouteRuleService;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * RouteRuleController
 *
 * @author pengyuhua
 * @date 2019/10/25
 */
@RestController
@RequestMapping(value = "/v1/route_rules")
public class RouteRuleController {
    private RouteRuleService routeRuleService;

    public RouteRuleController(RouteRuleService routeRuleService) {
        this.routeRuleService = routeRuleService;
    }

    @GetMapping
    @ApiOperation(value = "查询所有路由规则信息")
    @CustomPageRequest
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    public ResponseEntity<PageInfo<RouteRuleVO>> listRouteRules(@ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                                @RequestParam(value = "code", required = false) String code) {
        return new ResponseEntity<>(routeRuleService.listRouteRules(pageable, code), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "根据id查询路由详细信息")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    public ResponseEntity<RouteRuleVO> queryRouteRuleDetailById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(routeRuleService.queryRouteRuleDetailById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/")
    @ApiOperation(value = "添加路由规则")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    public ResponseEntity<RouteRuleVO> createRouteRule(@RequestBody @Validated({Insert.class}) RouteRuleVO routeRuleVO) {
        return new ResponseEntity<>(routeRuleService.createRouteRule(routeRuleVO), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation("根据ID删除路由规则")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    public ResponseEntity<Boolean> routeRuleDeleteById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(routeRuleService.deleteRouteRuleById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "更新路由规则信息")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    public ResponseEntity<RouteRuleVO>updateRouteRule(@PathVariable(value = "id") Long id, @RequestBody @Validated(Update.class) RouteRuleVO routeRuleVO) {
        return new ResponseEntity<>(routeRuleService.updateRouteRule(id, routeRuleVO), HttpStatus.OK);
    }

    @GetMapping("/check_code")
    @ApiOperation(value = "路由编码重复性校验")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    public ResponseEntity<Boolean> checkCode(String code) {
        return new ResponseEntity<>(routeRuleService.checkCode(code), HttpStatus.OK);
    }
}
