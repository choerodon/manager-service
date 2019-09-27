package io.choerodon.manager.infra.feign;

import io.choerodon.base.annotation.Permission;
import io.choerodon.manager.api.dto.MenuDTO;
import io.choerodon.manager.infra.dto.RouteDTO;
import io.choerodon.manager.infra.feign.fallback.ConfigServerClientFallback;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * iam service feign client
 * @author superlee
 * @since 2019-06-11
 */
@FeignClient(value = "base-service", fallback = ConfigServerClientFallback.class)
public interface IamClient {

    /**
     * 查询所有菜单
     * @return
     */
    @GetMapping("/v1/menus/list")
    ResponseEntity<List<MenuDTO>> list();

    @GetMapping("/v1/route")
    List<RouteDTO> selectRoute(@RequestParam(name = "name", required = false) String name);
}
