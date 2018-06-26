package io.choerodon.manager.app.service.impl;

import static io.choerodon.manager.infra.common.utils.VersionUtil.METADATA_VERSION;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.InstanceInfo;
import io.choerodon.core.domain.Page;
import io.choerodon.manager.api.dto.InstanceDetailDTO;
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

import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.InstanceDTO;
import io.choerodon.manager.app.service.InstanceService;
import io.choerodon.manager.infra.feign.ConfigServerClient;
import org.springframework.web.client.RestTemplate;

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
            Map<String, JsonNode> map = new HashMap<>(5);
            map.put("systemEnvironment", node.findValue("systemEnvironment"));
            map.put("applicationConfig: [classpath:/application.yml]", node.findValue("applicationConfig: [classpath:/application.yml]"));
            map.put("applicationConfig: [classpath:/bootstrap.yml]", node.findValue("applicationConfig: [classpath:/bootstrap.yml]"));
            instanceDetail.setEnvInfo(map);
            Map<String, JsonNode> map1 = new HashMap<>(5);
            map1.put("configService:configClient", node.findValue("configService:configClient"));
            String app = instanceDetail.getApp().toLowerCase();
            String key = "configService:" + app + "-service-default-null";
            map1.put(key, node.findValue(key));
            instanceDetail.setConfigInfo(map1);
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
    public Page<InstanceDTO> listByOptions(InstanceDTO queryInfo, PageRequest pageRequest) {
        Page<InstanceDTO> page = new Page<>();
        page.setSize(pageRequest.getSize());
        page.setNumber(pageRequest.getPage());
        List<InstanceDTO> serviceInstances = new ArrayList<>();
        if (StringUtils.isEmpty(queryInfo.getService())) {
            List<String> services = discoveryClient.getServices();
            if (services != null) {
                services.forEach( s -> serviceInstances.addAll(toInstanceDTOList(discoveryClient.getInstances(s))));
            }
        } else {
            serviceInstances.addAll(toInstanceDTOList(discoveryClient.getInstances(queryInfo.getService())));
        }
        List<InstanceDTO> instanceDTOS = filter(queryInfo, serviceInstances);
        instanceDTOS.sort(Comparator.comparing(InstanceDTO::getInstanceId));
        List<InstanceDTO> pageContent = getListPage(pageRequest.getPage(), pageRequest.getSize(), instanceDTOS);
        int pageSize = instanceDTOS.size() / pageRequest.getSize() + (instanceDTOS.size() % pageRequest.getSize() > 0 ? 1 : 0);
        page.setTotalPages(pageSize);
        page.setTotalElements(instanceDTOS.size());
        page.setNumberOfElements(pageContent.size());
        page.setContent(pageContent);
        return page;
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

    private List<InstanceDTO> filter(final InstanceDTO queryInfo, List<InstanceDTO> list) {
        if (queryInfo.getInstanceId() != null) {
            return list.stream().filter(t -> t.getInstanceId() != null && t.getInstanceId().contains(queryInfo.getInstanceId()))
                    .collect(Collectors.toList());
        } else if (queryInfo.getStatus() != null) {
            return list.stream().filter(t -> t.getStatus() != null && t.getStatus().contains(queryInfo.getStatus()))
                    .collect(Collectors.toList());
        } else if (queryInfo.getVersion() != null) {
            return list.stream().filter(t -> t.getVersion() != null && t.getVersion().contains(queryInfo.getVersion()))
                    .collect(Collectors.toList());
        } else if (queryInfo.getParams() != null) {
            return list.stream().filter(t -> t.getInstanceId() != null && t.getInstanceId().contains(queryInfo.getParams()) ||
                    t.getStatus() != null && t.getStatus().contains(queryInfo.getParams()) ||
                    t.getVersion() != null && t.getVersion().contains(queryInfo.getParams())
            ).collect(Collectors.toList());
        }
        return list;
    }

    private List<InstanceDTO> getListPage(int page, int pageSize, List<InstanceDTO> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        int totalCount = list.size();
        int fromIndex = page * pageSize;
        if (fromIndex >= totalCount) {
            return Collections.emptyList();
        }
        int toIndex = ((page + 1) * pageSize);
        if (toIndex > totalCount) {
            toIndex = totalCount;
        }
        return list.subList(fromIndex, toIndex);
    }
}
