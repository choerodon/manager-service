package io.choerodon.manager.app.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.InstanceInfo;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.InstanceDTO;
import io.choerodon.manager.api.dto.InstanceDetailDTO;
import io.choerodon.manager.api.dto.YamlDTO;
import io.choerodon.manager.infra.common.utils.config.ConfigUtil;
import io.choerodon.manager.app.service.InstanceService;
import io.choerodon.manager.infra.common.utils.ManualPageHelper;
import io.choerodon.manager.infra.feign.ConfigServerClient;
import io.choerodon.manager.infra.mapper.ConfigMapper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.choerodon.manager.infra.common.utils.VersionUtil.METADATA_VERSION;

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

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public InstanceServiceImpl(ConfigServerClient configServerClient,
                               DiscoveryClient discoveryClient,
                               ConfigMapper configMapper) {
        this.configServerClient = configServerClient;
        this.discoveryClient = discoveryClient;
        this.configMapper = configMapper;
    }

    @Override
    public InstanceDetailDTO query(String instanceId) {
        for (String service : discoveryClient.getServices()) {
            for (ServiceInstance serviceInstance : discoveryClient.getInstances(service)) {
                if (serviceInstance instanceof EurekaDiscoveryClient.EurekaServiceInstance) {
                    InstanceInfo instanceInfo =
                            ((EurekaDiscoveryClient.EurekaServiceInstance) serviceInstance).getInstanceInfo();
                    if (instanceInfo.getId().contains(instanceId)) {
                        return processInstanceDetail(instanceInfo);
                    }
                }

            }
        }
        return null;
    }

    private InstanceDetailDTO processInstanceDetail(InstanceInfo instanceInfo) {
        InstanceDetailDTO instanceDetail = new InstanceDetailDTO();
        instanceDetail.setInstanceId(instanceInfo.getInstanceId());
        instanceDetail.setHostName(instanceInfo.getHostName());
        instanceDetail.setIpAddr(instanceInfo.getIPAddr());
        instanceDetail.setApp(instanceInfo.getAppName());
        String[] array = instanceInfo.getInstanceId().split(":");
        instanceDetail.setPort(array[array.length - 1]);
        Map<String, String> metadata = instanceInfo.getMetadata();
        instanceDetail.setVersion(metadata.get(METADATA_VERSION));
        instanceDetail.setRegistrationTime(new Date(instanceInfo.getLeaseInfo().getRegistrationTimestamp() * 1000));
        instanceDetail.setMetadata(metadata);
        String healthCheckUrl = instanceInfo.getHealthCheckUrl();
        fetchEnvInfo(healthCheckUrl, instanceDetail);
        return instanceDetail;
    }

    private void fetchEnvInfo(String healthCheckUrl, InstanceDetailDTO instanceDetail) {
        String regex = "http://(\\d+\\.){3}\\d+:\\d+/";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(healthCheckUrl);
        String url;
        if (matcher.find()) {
            url = matcher.group();
        } else {
            throw new CommonException("error.illegal.management.url");
        }
        String envUrl = url + "env";
        ResponseEntity<String> response;
        try {
            response = restTemplate.getForEntity(envUrl, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                processEnvJson(instanceDetail, response.getBody());
            }
        } catch (Exception e) {
            LOGGER.info("can not fetch env info, request url : {}, exception message : {}", envUrl, e.getMessage());
        }
    }

    private void processEnvJson(InstanceDetailDTO instanceDetail, String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            Map<String, Object> map = new HashMap<>(5);
            map.put("systemEnvironment", node.findValue("systemEnvironment"));
            map.put("applicationConfig: [classpath:/application.yml]", node.findValue("applicationConfig: [classpath:/application.yml]"));
            map.put("applicationConfig: [classpath:/bootstrap.yml]", node.findValue("applicationConfig: [classpath:/bootstrap.yml]"));
            YamlDTO envInfoYml = new YamlDTO();
            String yaml = ConfigUtil.convertMapToText(map, "yaml");
            envInfoYml.setYaml(yaml);
            envInfoYml.setTotalLine(ConfigUtil.appearNumber(yaml, "\n") + 1);
            instanceDetail.setEnvInfoYml(envInfoYml);
            YamlDTO configInfoYml = new YamlDTO();
            Iterator<String> fieldNames = node.fieldNames();
            Map<String, Object> map1 = new HashMap<>();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                if (fieldName.contains("configService")) {
                    map1.put(fieldName, node.findValue(fieldName));
                }
            }
            String yaml1 = ConfigUtil.convertMapToText(map1, "yaml");
            configInfoYml.setYaml(yaml1);
            configInfoYml.setTotalLine(ConfigUtil.appearNumber(yaml, "\n") + 1);
            instanceDetail.setConfigInfoYml(configInfoYml);
        } catch (IOException e) {
            LOGGER.info("error.restTemplate.fetchEnvInfo {}", e.getMessage());
            throw new CommonException("error.parse.envJson");
        }
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

    @Override
    public Page<InstanceDTO> listByOptions(String service, Map<String, Object> map, PageRequest pageRequest) {
        List<InstanceDTO> serviceInstances = new ArrayList<>();
        if (StringUtils.isEmpty(service)) {
            List<String> services = discoveryClient.getServices();
            if (services != null) {
                services.forEach(s -> serviceInstances.addAll(toInstanceDTOList(discoveryClient.getInstances(s))));
            }
        } else {
            serviceInstances.addAll(toInstanceDTOList(discoveryClient.getInstances(service)));
        }

        return ManualPageHelper.postPage(serviceInstances, pageRequest, map);
    }

    private List<InstanceDTO> toInstanceDTOList(final List<ServiceInstance> serviceInstances) {
        List<InstanceDTO> instanceInfoList = new ArrayList<>();
        for (ServiceInstance serviceInstance : serviceInstances) {
            EurekaDiscoveryClient.EurekaServiceInstance eurekaServiceInstance =
                    (EurekaDiscoveryClient.EurekaServiceInstance) serviceInstance;
            InstanceInfo info = eurekaServiceInstance.getInstanceInfo();
            String instanceId = info.getInstanceId();
            String[] arr = instanceId.split(":");
            String pod = arr[arr.length - 1];
            String version = info.getMetadata().get(METADATA_VERSION);
            String status = info.getStatus().name();
            String serviceName = info.getAppName();
            //go语言registrationTimestamp的时间为10位，java版注册中心的registrationTimestamp的时间为13位，所以这里按服务器处理，自动乘以1000
            Date registrationTime = new Date(info.getLeaseInfo().getRegistrationTimestamp() * 1000);
            instanceInfoList.add(new InstanceDTO(instanceId, serviceName, version, status, pod, registrationTime));
        }
        return instanceInfoList;
    }

}
