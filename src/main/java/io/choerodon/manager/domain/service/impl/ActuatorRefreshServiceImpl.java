package io.choerodon.manager.domain.service.impl;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.manager.domain.service.IActuatorRefreshService;
import io.choerodon.manager.infra.dataobject.ActuatorDO;
import io.choerodon.manager.infra.mapper.ActuatorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActuatorRefreshServiceImpl implements IActuatorRefreshService {
    private static final String ACTUATOR_REFRESH_SAGA_CODE = "mgmt-actuator-refresh";
    @Autowired
    private ActuatorMapper actuatorMapper;
    @Autowired
    private TransactionalProducer producer;
    @Override
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
        return true;
    }

    @Override
    @Saga(code = ACTUATOR_REFRESH_SAGA_CODE, description = "刷新Actuator端口数据", inputSchemaClass = String.class)
    public void sendEvent(String json){
        producer.apply(StartSagaBuilder
                .newBuilder()
                .withLevel(ResourceLevel.SITE)
                .withRefType("application")
                .withSagaCode(ACTUATOR_REFRESH_SAGA_CODE), startSagaBuilder -> startSagaBuilder.withJson(json));
    }

}
