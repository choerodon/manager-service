package io.choerodon.manager.api.controller.v1;

import java.util.Optional;
import javax.validation.Valid;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.constant.PageConstant;
import io.choerodon.base.enums.ResourceType;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.manager.api.dto.RouteDTO;
import io.choerodon.manager.app.service.RouteService;
import io.choerodon.manager.infra.dataobject.RouteDO;
import io.choerodon.swagger.annotation.CustomPageRequest;

/**
 * 路由操作控制器
 *
 * @author wuguokai
 */
@RestController
@RequestMapping(value = "/v1/routes")
public class RouteController {

    private RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    public void setRouteService(RouteService routeService) {
        this.routeService = routeService;
    }

    /**
     * 分页查询路由信息
     *
     * @return page
     */
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("分页查询路由信息")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<PageInfo<RouteDTO>> list(@RequestParam(defaultValue = PageConstant.PAGE, required = false) final int page,
                                                   @RequestParam(defaultValue = PageConstant.SIZE, required = false) final int size,
                                                   @RequestParam(required = false, name = "name") String name,
                                                   @RequestParam(required = false, name = "path") String path,
                                                   @RequestParam(required = false, name = "serviceId") String serviceId,
                                                   @RequestParam(required = false, name = "builtIn") Boolean builtIn,
                                                   @RequestParam(required = false, name = "params") String params) {
        RouteDO routeDO = new RouteDO();
        routeDO.setName(name);
        routeDO.setPath(path);
        routeDO.setServiceId(serviceId);
        routeDO.setBuiltIn(builtIn);
        return new ResponseEntity<>(routeService.list(page,size, routeDO, params), HttpStatus.OK);
    }

    /**
     * 增加一个新路由
     *
     * @param routeDTO 路由信息对象
     * @return RouteDO
     */
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("增加一个新路由")
    @PostMapping
    public ResponseEntity<RouteDTO> create(@RequestBody @Valid RouteDTO routeDTO) {
        return new ResponseEntity<>(routeService.create(routeDTO), HttpStatus.OK);
    }

    /**
     * 更新一个路由
     *
     * @param routeDTO 路由对象
     * @return RouteDO
     */
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("更新一个路由")
    @PostMapping("/{route_id}")
    public ResponseEntity<RouteDTO> update(@PathVariable("route_id") Long id, @RequestBody RouteDTO routeDTO) {
        return new ResponseEntity<>(routeService.update(id, routeDTO), HttpStatus.OK);
    }

    /**
     * 根据routeId删除一个路由
     *
     * @param id 路由id
     * @return null
     */
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("根据routeId删除一个路由")
    @DeleteMapping(value = "/{route_id}")
    public ResponseEntity<Boolean> delete(@PathVariable("route_id") Long id) {
        return Optional.ofNullable(routeService.delete(id))
                .map(i -> new ResponseEntity<>(i, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.route.delete"));
    }

    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation(value = "route 校验接口")
    @PostMapping(value = "/check")
    public ResponseEntity check(@RequestBody RouteDTO routeDTO) {
        routeService.checkRoute(routeDTO);
        return new ResponseEntity(HttpStatus.OK);
    }


}
