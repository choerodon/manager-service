package io.choerodon.manager.infra.repository.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.domain.manager.entity.ServiceE;
import io.choerodon.manager.domain.repository.ServiceRepository;
import io.choerodon.manager.infra.dto.ServiceDTO;
import io.choerodon.manager.infra.mapper.ServiceMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wuguokai
 */
@Component
public class ServiceRepositoryImpl implements ServiceRepository {

    private ServiceMapper serviceMapper;

    public ServiceRepositoryImpl(ServiceMapper serviceMapper) {
        this.serviceMapper = serviceMapper;
    }

    @Override
    public ServiceE getService(Long serviceId) {
        return ConvertHelper.convert(serviceMapper.selectByPrimaryKey(serviceId), ServiceE.class);
    }

    @Override
    public ServiceE addService(ServiceE serviceE) {
        ServiceDTO serviceDTO = ConvertHelper.convert(serviceE, ServiceDTO.class);
        int isInsert = serviceMapper.insert(serviceDTO);
        if (isInsert != 1) {
            throw new CommonException("error.service.add");
        }
        return ConvertHelper.convert(serviceDTO, ServiceE.class);
    }

    @Override
    public ServiceE updateService(ServiceE serviceE) {
        ServiceDTO oldServiceDTO = serviceMapper.selectByPrimaryKey(serviceE.getId());
        ServiceDTO serviceDTO = ConvertHelper.convert(serviceE, ServiceDTO.class);
        serviceDTO.setObjectVersionNumber(oldServiceDTO.getObjectVersionNumber());
        int isUpdate = serviceMapper.updateByPrimaryKeySelective(serviceDTO);
        if (isUpdate != 1) {
            throw new CommonException("error.service.update");
        }
        return ConvertHelper.convert(serviceDTO, ServiceE.class);
    }

    @Override
    public boolean deleteService(Long serviceId) {
        int isDelete = serviceMapper.deleteByPrimaryKey(serviceId);
        if (isDelete != 1) {
            throw new CommonException("error.service.delete");
        }
        return true;
    }

    @Override
    public List<ServiceDTO> getAllService() {
        return serviceMapper.selectAll();
    }

    @Override
    public ServiceDTO getService(String serviceName) {
        return serviceMapper.selectOne(new ServiceDTO(serviceName));
    }

    @Override
    public List<ServiceDTO> selectServicesByFilter(String param) {
        return serviceMapper.selectServicesByFilter(param);
    }
}
