package io.choerodon.manager.infra.common.utils;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

/**
 * 对应用基于Discovery的常用行为的工具类.
 *
 * @author wuguokai
 */
@Component
public class DiscoveryUtil {

    private DiscoveryClient discoveryClient;

    public DiscoveryUtil(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    /**
     * 获取服务的标签集合
     *
     * @param serviceName 服务名
     * @return set
     */
    public Set<String> getServiceLabelSet(String serviceName) {
        Set<String> labelSet = discoveryClient.getInstances(serviceName)
                .stream().parallel()
                .map(serviceInstance -> serviceInstance.getMetadata().get("label"))
                .collect(Collectors.toSet());
        labelSet.removeAll(Collections.singleton(null));
        return labelSet;
    }
}
