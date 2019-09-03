package io.choerodon.manager.infra.feign;

import io.choerodon.manager.api.dto.MenuDTO;
import io.choerodon.manager.infra.feign.fallback.ConfigServerClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

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
}
