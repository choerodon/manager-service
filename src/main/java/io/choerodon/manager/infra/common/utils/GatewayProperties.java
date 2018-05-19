package io.choerodon.manager.infra.common.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * gateway类型的服务名称列表
 *
 * @author wuguokai
 */
@ConfigurationProperties(prefix = "choerodon.gateway")
public class GatewayProperties {
    private String[] names = new String[]{"api-gateway", "gateway-helper"};

    public String[] getNames() {
        return names;
    }

    public void setNames(String[] names) {
        this.names = names;
    }
}
