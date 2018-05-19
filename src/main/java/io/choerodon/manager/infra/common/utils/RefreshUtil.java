package io.choerodon.manager.infra.common.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.choerodon.manager.infra.feign.ConfigServerClient;

/**
 * 配置刷新操作
 *
 * @author wuguokai
 */
@Component
public class RefreshUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshUtil.class);
    private final ExecutorService asyncExecutor = Executors.newSingleThreadExecutor();
    private ConfigServerClient configServerClient;

    public RefreshUtil(ConfigServerClient configServerClient) {
        this.configServerClient = configServerClient;
    }

    /**
     * 通知config-server刷新配置
     *
     * @param path 修改的配置路径
     */
    public void refresh(String path) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("path", path);
        LOGGER.info("{} :配置刷新通知", path);
        asyncExecutor.submit(() ->
                configServerClient.refresh(map)
        );
    }
}
