package io.choerodon.manager.app.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.InstanceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedNames;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.InstanceDTO;
import io.choerodon.manager.api.dto.InstanceDetailDTO;
import io.choerodon.manager.api.dto.YamlDTO;
import io.choerodon.manager.app.service.InstanceService;
import io.choerodon.manager.infra.common.utils.ManualPageHelper;
import io.choerodon.manager.infra.common.utils.config.ConfigUtil;
import io.choerodon.manager.infra.feign.ConfigServerClient;
import io.choerodon.manager.infra.mapper.ConfigMapper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
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
        String[] str = instanceId.split(":");
        if (str.length != 3) {
            throw new CommonException("error.illegal.instanceId");
        }
        instanceId = str[1] + ":" + str[0] + ":" + str[2];
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
            } else {
                throw new CommonException("error.config.fetchEnv");
            }
        } catch (Exception e) {
            LOGGER.warn("can not fetch env info, request url : {}, exception message : {}", envUrl, e.getMessage());
            throw new CommonException("error.config.fetchEnv");
        }
    }

    private void processEnvJson(InstanceDetailDTO instanceDetail, String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            String allConfigYaml = getAllConfigYaml(node);
            instanceDetail.setEnvInfoYml(new YamlDTO(allConfigYaml, ConfigUtil.appearNumber(allConfigYaml, "\n") + 1));
            String activeConfigYaml = getActiveConfigYaml(node);
            instanceDetail.setConfigInfoYml(new YamlDTO(activeConfigYaml, ConfigUtil.appearNumber(activeConfigYaml, "\n") + 1));
        } catch (IOException e) {
            LOGGER.info("error.restTemplate.fetchEnvInfo {}", e.getMessage());
            throw new CommonException("error.parse.envJson");
        }
    }

    private String getAllConfigYaml(final JsonNode root) {
        Map<String, Object> map = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> it = root.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> entry = it.next();
            String key = entry.getKey();
            if (key.startsWith("applicationConfig: [classpath:/")) {
                key = key.replace("applicationConfig: [classpath:/", "").replace(".properties]", "")
                        .replace(".yml]", "");
            }
            if (key.startsWith("configService:")) {
                key = "config-server";
            }
            Iterator<Map.Entry<String, JsonNode>> vit = entry.getValue().fields();
            while (vit.hasNext()) {
                Map.Entry<String, JsonNode> value = vit.next();
                if (value.getValue().isValueNode()) {
                    String jsonValue = value.getValue().asText();
                    if (!StringUtils.isEmpty(jsonValue) && !value.getKey().startsWith("java")) {
                        map.put(key + "." + value.getKey(), value.getValue().asText());
                    }
                }
            }
        }
        return ConfigUtil.convertMapToText(map, "yaml");
    }

    private String getActiveConfigYaml(final JsonNode root) {
        String config = getConfigPropertySource(root);
        String activeProfile = "default";
        JsonNode profileNode = root.findValue("profiles");
        if (profileNode != null && !profileNode.asText().isEmpty()) {
            activeProfile = profileNode.asText();
        }
        activeProfile = "applicationConfig: [classpath:/application-" + activeProfile;
        Map<String, Data> map = PropertySourceBuilder.newInstance(root)
                .appendApply("defaultProperties")
                .appendApply("applicationConfig: [classpath:/bootstrap.properties]")
                .appendApply("applicationConfig: [classpath:/bootstrap.yml]")
                .appendApply("kafkaBinderDefaultProperties")
                .appendApply("applicationConfig: [classpath:/application.properties]")
                .appendApply("applicationConfig: [classpath:/application.yml]")
                .appendApply(activeProfile + ".properties]")
                .appendApply(activeProfile + ".yml]")
                .appendApply("random")
                .appendApply(config)
                .coverApply("systemEnvironment")
                .coverApply("systemProperties")
                .appendApply("servletConfigInitParams")
                .appendApply("commandLineArgs")
                .coverApplyServerPort()
                .data();
        return ConfigUtil.convertDataMapToYaml(map);
    }

    private static class PropertySourceBuilder {

        private final JsonNode root;
        private final Map<String, Data> map = new HashMap<>();

        public PropertySourceBuilder(JsonNode node) {
            this.root = node;
        }

        public static PropertySourceBuilder newInstance(final JsonNode node) {
            return new PropertySourceBuilder(node);
        }


        public PropertySourceBuilder appendApply(final String property) {
            if (property == null) {
                return this;
            }
            JsonNode value = root.findValue(property);
            if (value == null) {
                return this;
            }
            Iterator<Map.Entry<String, JsonNode>> it = value.fields();

            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                Data data = getDataByRelaxedNames(entry.getKey());
                if (data == null) {
                    map.put(entry.getKey(), new Data(entry.getValue().asText(), property));
                } else {
                    data.setValue(entry.getValue().asText());
                }
            }
            return this;
        }


        public PropertySourceBuilder coverApplyServerPort() {
            JsonNode value = root.findValue("server.ports");
            if (value == null) {
                return this;
            }
            JsonNode serverPort = value.findValue("local.server.port");
            if (serverPort != null) {
                map.put("server.port", new Data(serverPort.asText(), "server.ports"));
            }
            JsonNode managementServerPort = value.findValue("local.management.port");
            if (serverPort != null) {
                map.put("management.port", new Data(managementServerPort.asText(), "server.ports"));
            }
            return this;
        }

        public PropertySourceBuilder coverApply(final String property) {
            if (property == null) {
                return this;
            }
            JsonNode value = root.findValue(property);
            if (value == null) {
                return this;
            }
            Iterator<Map.Entry<String, JsonNode>> it = value.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                String mapExistKey = getKeyByRelaxedNames(entry.getKey());
                if (mapExistKey != null) {
                    Data data = map.get(mapExistKey);
                    data.setValue(entry.getValue().asText());
                }
            }
            return this;
        }

        public Map<String, Data> data() {
            return this.map;
        }


        private String getKeyByRelaxedNames(final String key) {
            for (String i : map.keySet()) {
                RelaxedNames relaxedNames = RelaxedNames.forCamelCase(i);
                for (String j : relaxedNames) {
                    if (j.contains(key)) {
                        return i;
                    }
                }
            }
            return null;
        }

        private Data getDataByRelaxedNames(final String key) {
            RelaxedNames relaxedNames = RelaxedNames.forCamelCase(key);
            for (String i : relaxedNames) {
                if (map.containsKey(i)) {
                    return map.get(i);
                }
            }
            return null;
        }

    }

    public static class Data {
        private Object value;
        private String source;

        public Data(Object value, String source) {
            this.value = value;
            this.source = source;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "value=" + value +
                    ", source='" + source + '\'' +
                    '}';
        }
    }

    private String getConfigPropertySource(final JsonNode root) {
        Iterator<Map.Entry<String, JsonNode>> it = root.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> entry = it.next();
            if (entry.getKey().startsWith("configService:")) {
                return entry.getKey();
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
            if (info.getAppName().equalsIgnoreCase("go-register-server")) {
                continue;
            }
            String instanceId = info.getInstanceId();
            String[] arr = instanceId.split(":");
            String pod = arr[arr.length - 1];
            String version = info.getMetadata().get(METADATA_VERSION);
            String status = info.getStatus().name();
            String serviceName = info.getAppName();
            instanceId = arr[1] + ":" + arr[0] + ":" + pod;
            //go语言registrationTimestamp的时间为10位，java版注册中心的registrationTimestamp的时间为13位，所以这里按服务器处理，自动乘以1000
            Date registrationTime = new Date(info.getLeaseInfo().getRegistrationTimestamp() * 1000);
            instanceInfoList.add(new InstanceDTO(instanceId, serviceName, version, status, pod, registrationTime));
        }
        return instanceInfoList;
    }

}
