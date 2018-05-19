package io.choerodon.manager.infra.feign;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.choerodon.manager.infra.feign.fallback.ConfigServerClientFallback;

/**
 * 向config-server服务发送feign请求，告知修改的配置
 *
 * @author wuguokai
 */
@FeignClient(value = "config-server", fallback = ConfigServerClientFallback.class)
public interface ConfigServerClient {
    @RequestMapping(value = "/monitor", method = RequestMethod.POST)
    ResponseEntity<String> refresh(@RequestBody Map<String, ?> queryMap);
}


