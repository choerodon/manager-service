package io.choerodon.manager.api.controller.v1;

import javax.validation.Valid;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.base.domain.Sort;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.manager.infra.dto.RouteDTO;
import io.choerodon.mybatis.annotation.SortDefault;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.manager.app.service.RouteService;
import springfox.documentation.annotations.ApiIgnore;

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
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<PageInfo<RouteDTO>> list(@ApiIgnore
                                                   @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                   @RequestParam(required = false, name = "name") String name,
                                                   @RequestParam(required = false, name = "path") String path,
                                                   @RequestParam(required = false, name = "serviceId") String serviceId,
                                                   @RequestParam(required = false, name = "builtIn") Boolean builtIn,
                                                   @RequestParam(required = false, name = "params") String params) {
        RouteDTO routeDTO = new RouteDTO();
        routeDTO.setName(name);
        routeDTO.setPath(path);
        routeDTO.setServiceId(serviceId);
        routeDTO.setBuiltIn(builtIn);
        return new ResponseEntity<>(routeService.list(pageRequest, routeDTO, params), HttpStatus.OK);
    }

    /**
     * 增加一个新路由
     *
     * @param routeDTO 路由信息对象
     * @return RouteDTO
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
     * @return RouteDTO
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
    public ResponseEntity delete(@PathVariable("route_id") Long id) {
        routeService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation(value = "route 校验接口")
    @PostMapping(value = "/check")
    public ResponseEntity check(@RequestBody RouteDTO routeDTO) {
        routeService.checkRoute(routeDTO);
        return new ResponseEntity(HttpStatus.OK);
    }


}
