package io.choerodon.manager.domain.manager.converter;

import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.manager.api.dto.ServiceDTO;
import io.choerodon.manager.domain.factory.ServiceEFactory;
import io.choerodon.manager.domain.manager.entity.ServiceE;
import io.choerodon.manager.infra.dataobject.ServiceDO;

/**
 * @author superleader8@gmail.com
 * @author wuguokai
 */
@Component
public class ServiceConverter implements ConvertorI<ServiceE, ServiceDO, ServiceDTO> {
    @Override
    public ServiceE doToEntity(ServiceDO dataObject) {
        ServiceE se = ServiceEFactory.createServiceE();
        se.setId(dataObject.getId());
        se.setName(dataObject.getName());
        se.setObjectVersionNumber(dataObject.getObjectVersionNumber());
        return se;
    }

    @Override
    public ServiceDO entityToDo(ServiceE entity) {
        ServiceDO serviceDO = new ServiceDO();
        serviceDO.setId(entity.getId());
        serviceDO.setName(entity.getName());
        serviceDO.setObjectVersionNumber(entity.getObjectVersionNumber());
        return serviceDO;
    }

    @Override
    public ServiceE dtoToEntity(ServiceDTO serviceDTO) {
        ServiceE se = ServiceEFactory.createServiceE();
        se.setId(serviceDTO.getId());
        se.setName(serviceDTO.getName());
        return se;
    }

    @Override
    public ServiceDTO entityToDto(ServiceE serviceE) {
        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setId(serviceE.getId());
        serviceDTO.setName(serviceE.getName());
        return serviceDTO;
    }
}
