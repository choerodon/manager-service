package io.choerodon.manager.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.manager.app.service.ActuatorRefreshService;
import io.choerodon.manager.infra.dto.ActuatorDTO;
import io.choerodon.manager.infra.mapper.ActuatorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ActuatorRefreshServiceImpl implements ActuatorRefreshService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActuatorRefreshServiceImpl.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String ACTUATOR_REFRESH_SAGA_CODE = "mgmt-actuator-refresh";
    private static final String METADATA_REFRESH_SAGA_CODE = "mgmt-metadata-refresh";
    private static final String SERVICE = "service";
    private ActuatorMapper actuatorMapper;
    private TransactionalProducer producer;

    public ActuatorRefreshServiceImpl(ActuatorMapper actuatorMapper, TransactionalProducer producer) {
        this.actuatorMapper = actuatorMapper;
        this.producer = producer;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrInsertActuator(String serviceName, String serviceVersion, String json) {
        ActuatorDTO example = new ActuatorDTO();
        example.setServiceName(serviceName);
        example.setServiceVersion(serviceVersion);
        ActuatorDTO actuator = actuatorMapper.selectOne(example);
        if (actuator != null){
            if (actuator.getValue().equals(json)){
                return false;
            }
            actuator.setValue(json);
            actuator.setStatus(ActuatorDTO.STATUS_PREPARED);
            actuatorMapper.updateByPrimaryKey(actuator);
        } else {
            example.setValue(json);
            example.setStatus(ActuatorDTO.STATUS_PREPARED);
            actuatorMapper.insert(example);
        }
        return true;
    }

    /**
     * 每10秒尝试发送一次准备好的数据
     */
    @Scheduled(fixedDelay = 10000)
    @SuppressWarnings("unchecked")
    @Saga(code = ACTUATOR_REFRESH_SAGA_CODE, description = "刷新Actuator端口数据", inputSchemaClass = String.class)
    public void processActuatorScheduled() {
        ActuatorDTO example = new ActuatorDTO();
        example.setStatus(ActuatorDTO.STATUS_PREPARED);
        List<ActuatorDTO> preparedActuators = actuatorMapper.select(example);
        for (ActuatorDTO actuator : preparedActuators){
            try {
                Map jsonMap = OBJECT_MAPPER.readValue(actuator.getValue(), Map.class);
                jsonMap.put(SERVICE, actuator.getServiceName());
                producer.apply(StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.SITE)
                        .withRefType(SERVICE)
                        .withRefId(actuator.getServiceName())
                        .withSagaCode(ACTUATOR_REFRESH_SAGA_CODE), startSagaBuilder -> startSagaBuilder.withPayloadAndSerialize(jsonMap));
                actuator.setStatus(ActuatorDTO.STATUS_PROCESSED);
                actuatorMapper.updateByPrimaryKey(actuator);
                LOGGER.info("start actuator saga success {}", actuator.getServiceName());
            } catch (IOException e) {
                LOGGER.warn("actuator send event exception", e);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @Saga(code = METADATA_REFRESH_SAGA_CODE, description = "刷新Metadata端口数据", inputSchemaClass = String.class)
    public void sendMetadataEvent(String json, String service){
        try {
            Map jsonMap = OBJECT_MAPPER.readValue(json, Map.class);
            jsonMap.put(SERVICE, service);
            producer.apply(StartSagaBuilder
                    .newBuilder()
                    .withLevel(ResourceLevel.SITE)
                    .withRefType(SERVICE)
                    .withRefId(service)
                    .withSagaCode(METADATA_REFRESH_SAGA_CODE), startSagaBuilder -> startSagaBuilder.withPayloadAndSerialize(jsonMap));
        } catch (IOException e) {
            LOGGER.warn("actuator send event exception", e);
        }
    }



    /**
     * 驼峰格式字符串转换为中划线格式字符串
     *
     * @param param 驼峰形式的字符串 (eg. UserCodeController)
     * @return 中划线形式的字符串 (eg. user-code-controller)
     */
    public static String camelToHyphenLine(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0){
                    sb.append('-');
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
