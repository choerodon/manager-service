package io.choerodon.manager.api.controller.v1;

import java.util.Optional;

import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.manager.api.dto.RouteDTO;
import io.choerodon.manager.app.service.RouteService;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

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

    /**
     * 分页查询路由信息
     *
     * @return page
     */
    @Permission(level = ResourceLevel.SITE, roles = {"managerAdmin"})
    @ApiOperation("分页查询路由信息")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<Page<RouteDTO>> list(@ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.ASC) PageRequest pageRequest) {
        return new ResponseEntity<>(routeService.list(pageRequest), HttpStatus.OK);
    }

    /**
     * 增加一个新路由
     *
     * @param routeDTO 路由信息对象
     * @return RouteDO
     */
    @Permission(level = ResourceLevel.SITE, roles = {"managerAdmin"})
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
    @Permission(level = ResourceLevel.SITE, roles = {"managerAdmin"})
    @ApiOperation("更新一个路由")
    @PostMapping("/{id}")
    public ResponseEntity<RouteDTO> update(@PathVariable("id") Long id, @RequestBody RouteDTO routeDTO) {
        return new ResponseEntity<>(routeService.update(id, routeDTO), HttpStatus.OK);
    }

    /**
     * 根据routeId删除一个路由
     *
     * @param id 路由id
     * @return null
     */
    @Permission(level = ResourceLevel.SITE, roles = {"managerAdmin"})
    @ApiOperation("根据routeId删除一个路由")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("id") Long id) {
        return Optional.ofNullable(routeService.delete(id))
                .map(i -> new ResponseEntity<>(i, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.route.delete"));
    }

}
