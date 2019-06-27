package io.choerodon.manager.domain.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.manager.domain.service.IActuatorRefreshService;
import io.choerodon.manager.infra.dataobject.ActuatorDO;
import io.choerodon.manager.infra.mapper.ActuatorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

@Service
public class ActuatorRefreshServiceImpl implements IActuatorRefreshService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActuatorRefreshServiceImpl.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String ACTUATOR_REFRESH_SAGA_CODE = "mgmt-actuator-refresh";
    private static final String METADATA_REFRESH_SAGA_CODE = "mgmt-metadata-refresh";
    private ActuatorMapper actuatorMapper;
    private TransactionalProducer producer;
    private IActuatorRefreshService actuatorRefreshService;

    public ActuatorRefreshServiceImpl(ActuatorMapper actuatorMapper, TransactionalProducer producer,@Lazy IActuatorRefreshService actuatorRefreshService) {
        this.actuatorMapper = actuatorMapper;
        this.producer = producer;
        this.actuatorRefreshService = actuatorRefreshService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrInsertActuator(String serviceName, String serviceVersion, String json) {
        ActuatorDO example = new ActuatorDO();
        example.setServiceName(serviceName);
        example.setServiceVersion(serviceVersion);
        ActuatorDO actuator = actuatorMapper.selectOne(example);
        if (actuator != null){
            if (actuator.getValue().equals(json)){
                return false;
            }
            actuator.setValue(json);
            actuatorMapper.updateByPrimaryKey(actuator);
        } else {
            example.setValue(json);
            actuatorMapper.insert(example);
        }
        actuatorRefreshService.sendActuatorEvent(json, serviceName);
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Saga(code = ACTUATOR_REFRESH_SAGA_CODE, description = "刷新Actuator端口数据", inputSchemaClass = String.class)
    public void sendActuatorEvent(String json, String service){
        try {
            Map jsonMap = OBJECT_MAPPER.readValue(json, Map.class);
            jsonMap.put("service", service);
            producer.apply(StartSagaBuilder
                    .newBuilder()
                    .withLevel(ResourceLevel.SITE)
                    .withRefType("service")
                    .withRefId(service)
                    .withSagaCode(ACTUATOR_REFRESH_SAGA_CODE), startSagaBuilder -> startSagaBuilder.withPayloadAndSerialize(jsonMap));
        } catch (IOException e) {
            LOGGER.warn("actuator send event exception", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @Saga(code = METADATA_REFRESH_SAGA_CODE, description = "刷新Metadata端口数据", inputSchemaClass = String.class)
    public void sendMetadataEvent(String json, String service){
        try {
            Map jsonMap = OBJECT_MAPPER.readValue(json, Map.class);
            jsonMap.put("service", service);
            producer.apply(StartSagaBuilder
                    .newBuilder()
                    .withLevel(ResourceLevel.SITE)
                    .withRefType("service")
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
