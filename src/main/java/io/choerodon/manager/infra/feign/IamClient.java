package io.choerodon.manager.infra.feign;

import com.github.pagehelper.PageInfo;
import io.choerodon.manager.api.dto.MenuDTO;
import io.choerodon.manager.api.dto.RouteRuleDTO;
import io.choerodon.manager.api.dto.RouteRuleVO;
import io.choerodon.manager.infra.dto.RouteDTO;
import io.choerodon.manager.infra.feign.fallback.IamClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * iam service feign client
 * @author superlee
 * @since 2019-06-11
 */
@FeignClient(value = "base-service", fallback = IamClientFallback.class)
public interface IamClient {

    /**
     * 查询所有菜单
     * @return
     */
    @GetMapping("/v1/menus/list")
    ResponseEntity<List<MenuDTO>> list();

    @GetMapping("/v1/route")
    List<RouteDTO> selectRoute(@RequestParam(name = "name", required = false) String name);

    /**
     * 查询路由规则信息
     * @param pageable
     * @param code
     * @return
     */
    @GetMapping("/v1/route_rules")
    ResponseEntity<PageInfo<RouteRuleVO>> listRouteRules(@RequestBody Pageable pageable, @RequestParam(value = "code", required = false) String code);

    /**
     * 查询路由规则详细信息
     * @param id   路由id
     * @return
     */
    @GetMapping("/v1/route_rules/{id}")
    ResponseEntity<RouteRuleVO> queryRouteRuleDetailById(@PathVariable("id") Long id);

    /**
     * 新增路由规则
     * @param routeRuleVO
     * @return
     */
    @PostMapping("/v1/route_rules/insert")
    ResponseEntity<RouteRuleVO> insertRouteRule(@RequestBody RouteRuleVO routeRuleVO);

    /**
     * 删除路由规则
     * @param id
     * @return
     */
    @DeleteMapping("/v1/route_rules/{id}")
    ResponseEntity<Boolean> deleteRouteRuleById(@PathVariable("id") Long id);

    /**
     * 更新路由规则
     * @param routeRuleVO
     * @return
     */
    @PostMapping("/v1/route_rules/update")
    ResponseEntity<RouteRuleVO>updateRouteRule(@RequestBody RouteRuleVO routeRuleVO);

    /**
     * 路由校验(code唯一性)
     * @param routeRuleDTO
     * @return
     */
    @PostMapping("/v1/route_rules/check")
    ResponseEntity<Boolean> checkCode(@RequestBody RouteRuleDTO routeRuleDTO);

}
