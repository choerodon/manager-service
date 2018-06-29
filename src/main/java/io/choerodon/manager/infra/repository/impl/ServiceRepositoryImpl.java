package io.choerodon.manager.infra.repository.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.domain.manager.entity.ServiceE;
import io.choerodon.manager.domain.repository.ServiceRepository;
import io.choerodon.manager.infra.dataobject.ServiceDO;
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
        ServiceDO serviceDO = ConvertHelper.convert(serviceE, ServiceDO.class);
        int isInsert = serviceMapper.insert(serviceDO);
        if (isInsert != 1) {
            throw new CommonException("error.service.add");
        }
        return ConvertHelper.convert(serviceDO, ServiceE.class);
    }

    @Override
    public ServiceE updateService(ServiceE serviceE) {
        ServiceDO oldServiceDO = serviceMapper.selectByPrimaryKey(serviceE.getId());
        ServiceDO serviceDO = ConvertHelper.convert(serviceE, ServiceDO.class);
        serviceDO.setObjectVersionNumber(oldServiceDO.getObjectVersionNumber());
        int isUpdate = serviceMapper.updateByPrimaryKeySelective(serviceDO);
        if (isUpdate != 1) {
            throw new CommonException("error.service.update");
        }
        return ConvertHelper.convert(serviceDO, ServiceE.class);
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
    public List<ServiceDO> getAllService() {
        return serviceMapper.selectAll();
    }

    @Override
    public ServiceDO getService(String serviceName) {
        return serviceMapper.selectOne(new ServiceDO(serviceName));
    }

    @Override
    public List<ServiceDO> selectServicesByFilter(String param) {
        return serviceMapper.selectServicesByFilter(param);
    }
}
