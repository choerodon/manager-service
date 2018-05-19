package io.choerodon.manager.app.assembler;

import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.manager.api.dto.RegisterInstancePayload;
import io.choerodon.manager.domain.manager.entity.ServiceInstanceE;

/**
 * {@inheritDoc}
 *
 * @author superleader8@gmail.com
 * @data 2018/3/13
 */
@Component
public class InstanceAssembler implements ConvertorI<ServiceInstanceE, Object, RegisterInstancePayload> {

    @Override
    public ServiceInstanceE dtoToEntity(RegisterInstancePayload registerInstancePayload) {
        ServiceInstanceE serviceInstanceE = new ServiceInstanceE();
        serviceInstanceE.setApiData(registerInstancePayload.getApiData());
        serviceInstanceE.setAppName(registerInstancePayload.getAppName());
        serviceInstanceE.setId(registerInstancePayload.getId());
        serviceInstanceE.setStatus(registerInstancePayload.getStatus());
        serviceInstanceE.setUuid(registerInstancePayload.getUuid());
        serviceInstanceE.setVersion(registerInstancePayload.getVersion());
        return serviceInstanceE;
    }

    @Override
    public RegisterInstancePayload entityToDto(ServiceInstanceE serviceInstanceE) {
        RegisterInstancePayload registerInstancePayload = new RegisterInstancePayload();
        registerInstancePayload.setApiData(serviceInstanceE.getApiData());
        registerInstancePayload.setAppName(serviceInstanceE.getAppName());
        registerInstancePayload.setId(serviceInstanceE.getId());
        registerInstancePayload.setStatus(serviceInstanceE.getStatus());
        registerInstancePayload.setUuid(serviceInstanceE.getUuid());
        registerInstancePayload.setVersion(serviceInstanceE.getVersion());
        return registerInstancePayload;
    }
}
