package io.choerodon.manager.app.service.impl;

import static io.choerodon.manager.infra.common.utils.VersionUtil.METADATA_VERSION;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.netflix.appinfo.InstanceInfo;
import io.choerodon.manager.infra.mapper.ConfigMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.InstanceDTO;
import io.choerodon.manager.app.service.InstanceService;
import io.choerodon.manager.infra.feign.ConfigServerClient;

/**
 * @author flyleft
 */
@Service
public class InstanceServiceImpl implements InstanceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceService.class);

    private final ExecutorService asyncExecutor = Executors.newSingleThreadExecutor();

    private static final String CONFIG_VERSION_DEFAULT = "default";

    private ConfigServerClient configServerClient;

    private DiscoveryClient discoveryClient;

    private ConfigMapper configMapper;

    public InstanceServiceImpl(ConfigServerClient configServerClient,
                               DiscoveryClient discoveryClient,
                               ConfigMapper configMapper) {
        this.configServerClient = configServerClient;
        this.discoveryClient = discoveryClient;
        this.configMapper = configMapper;
    }

    @Override
    public List<InstanceDTO> list(String service) {
        List<InstanceDTO> instanceInfoList = new ArrayList<>();
        if (StringUtils.isEmpty(service)) {
            discoveryClient.getServices().forEach(t -> addIntoInstanceInfoList(instanceInfoList, t));
        } else {
            addIntoInstanceInfoList(instanceInfoList, service);
        }
        return instanceInfoList;
    }

    private void addIntoInstanceInfoList(final List<InstanceDTO> instanceInfoList, final String service) {
        discoveryClient.getInstances(service).stream()
                .filter(t -> t instanceof EurekaDiscoveryClient.EurekaServiceInstance)
                .forEach(t -> {
                    InstanceInfo info = ((EurekaDiscoveryClient.EurekaServiceInstance) t).getInstanceInfo();
                    String instanceId = info.getInstanceId();
                    String[] arr = instanceId.split(":");
                    String pod = arr[arr.length - 1];
                    String version = info.getMetadata().get(METADATA_VERSION);
                    String status = info.getStatus().name();
                    String serviceName = info.getAppName();
                    Date registrationTime = new Date(info.getLeaseInfo().getRegistrationTimestamp());
                    instanceInfoList.add(new InstanceDTO(instanceId, serviceName, version, status, pod, registrationTime));
                });
    }

    @Override
    public InstanceInfo query(String instanceId) {
        for (String service : discoveryClient.getServices()) {
            for (ServiceInstance serviceInstance : discoveryClient.getInstances(service)) {
                if (serviceInstance instanceof EurekaDiscoveryClient.EurekaServiceInstance) {
                    InstanceInfo instanceInfo =
                            ((EurekaDiscoveryClient.EurekaServiceInstance) serviceInstance).getInstanceInfo();
                    if (instanceInfo.getId().contains(instanceId)) {
                        return instanceInfo;
                    }
                }

            }
        }
        return null;
    }

    @Override
    public void update(String instanceId, Long configId) {
        String[] strings = instanceId.split(":");
        if (strings.length != 3 || StringUtils.isEmpty(strings[1])) {
            throw new CommonException("error.instance.updateConfig.badParameter");
        }
        String configVersion = configMapper.selectConfigVersionById(configId);
        if (StringUtils.isEmpty(configVersion)) {
            configVersion = CONFIG_VERSION_DEFAULT;
        }
        Map<String, String> map = new LinkedHashMap<>();
        map.put("path", strings[1]);
        map.put("instanceId", instanceId);
        map.put("configVersion", configVersion);
        LOGGER.info("service {}  instance {} configVersion {} 配置刷新通知", strings[1], instanceId, configVersion);
        asyncExecutor.submit(() ->
                configServerClient.refresh(map)
        );
    }
}
