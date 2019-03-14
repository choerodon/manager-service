package io.choerodon.manager.infra.feign;

import io.choerodon.manager.infra.feign.fallback.ConfigServerClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 向config-server服务发送feign请求，告知修改的配置
 *
 * @author wuguokai
 */
@FeignClient(value = "config-server", fallback = ConfigServerClientFallback.class)
public interface ConfigServerClient {
    @PostMapping("/monitor")
    ResponseEntity<String> refresh(@RequestBody Map<String, ?> queryMap);
}


