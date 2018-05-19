package io.choerodon.manager.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.ServiceConfigDTO;
import io.choerodon.manager.domain.manager.entity.ServiceConfigE;
import io.choerodon.manager.domain.repository.ServiceConfigRepository;
import io.choerodon.manager.infra.dataobject.ServiceConfigDO;
import io.choerodon.manager.infra.mapper.ServiceConfigMapper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author wuguokai
 */
@Component
public class ServiceConfigRepositoryImpl implements ServiceConfigRepository {
    private static final String COMMON_EXCEPTION_1 = "error.config.not.exist";
    private ServiceConfigMapper serviceConfigMapper;

    public ServiceConfigRepositoryImpl(ServiceConfigMapper serviceConfigMapper) {
        this.serviceConfigMapper = serviceConfigMapper;
    }

    @Override
    public Page<ServiceConfigDTO> listByServiceId(Long serviceId, PageRequest pageRequest) {
        ServiceConfigDO serviceConfigDO = new ServiceConfigDO();
        serviceConfigDO.setServiceId(serviceId);
        Page<ServiceConfigDO> serviceConfigDOPage =
                PageHelper.doPageAndSort(pageRequest, () -> serviceConfigMapper.select(serviceConfigDO));
        return ConvertPageHelper.convertPage(serviceConfigDOPage, ServiceConfigDTO.class);
    }

    @Override
    public Page<ServiceConfigDTO> list(PageRequest pageRequest) {
        return ConvertPageHelper.convertPage(PageHelper.doPageAndSort(pageRequest,
                () -> serviceConfigMapper.selectAll()), ServiceConfigDTO.class);
    }

    @Override
    @Transactional
    public ServiceConfigE setConfigDefault(Long configId) {
        ServiceConfigDO serviceConfigDO = serviceConfigMapper.selectByPrimaryKey(configId);
        if (serviceConfigDO == null) {
            throw new CommonException(COMMON_EXCEPTION_1);
        }
        ServiceConfigE serviceConfigE = ConvertHelper.convert(serviceConfigDO, ServiceConfigE.class);
        serviceConfigE.setItDefault();
        serviceConfigDO = ConvertHelper.convert(serviceConfigE, ServiceConfigDO.class);
        serviceConfigMapper.closeDefaultByServiceId(serviceConfigDO.getServiceId());
        if (serviceConfigMapper.updateByPrimaryKeySelective(serviceConfigDO) != 1) {
            throw new CommonException("error.config.set.default");
        }
        serviceConfigDO = serviceConfigMapper.selectByPrimaryKey(serviceConfigDO.getId());
        return ConvertHelper.convert(serviceConfigDO, ServiceConfigE.class);
    }

    @Override
    public ServiceConfigE query(Long serviceConfigId) {
        return ConvertHelper.convert(serviceConfigMapper.selectByPrimaryKey(serviceConfigId), ServiceConfigE.class);
    }

    @Override
    public boolean delete(Long configId) {
        if (serviceConfigMapper.selectByPrimaryKey(configId) == null) {
            throw new CommonException(COMMON_EXCEPTION_1);
        }
        int isDelete = serviceConfigMapper.deleteByPrimaryKey(configId);
        if (isDelete != 1) {
            throw new CommonException("error.config.delete");
        }
        return true;
    }

    @Override
    public ServiceConfigE update(Long configId, ServiceConfigE serviceConfigE) {
        if (serviceConfigE.getObjectVersionNumber() == null) {
            throw new CommonException("error.objectVersionNumber.null");
        }
        if (serviceConfigMapper.selectByPrimaryKey(configId) == null) {
            throw new CommonException(COMMON_EXCEPTION_1);
        }
        ServiceConfigDO serviceConfigDO = ConvertHelper.convert(serviceConfigE, ServiceConfigDO.class);
        serviceConfigDO.setId(configId);
        if (serviceConfigMapper.updateByPrimaryKeySelective(serviceConfigDO) != 1) {
            throw new CommonException("error.config.update");
        }
        serviceConfigDO = serviceConfigMapper.selectByPrimaryKey(serviceConfigDO.getId());
        return ConvertHelper.convert(serviceConfigDO, ServiceConfigE.class);
    }

    @Override
    public ServiceConfigDTO queryDefaultByServiceName(String serviceName) {
        return ConvertHelper.convert(serviceConfigMapper.selectOneByServiceDefault(serviceName),
                ServiceConfigDTO.class);
    }

    @Override
    public ServiceConfigDTO queryByServiceNameAndConfigVersion(String serviceName, String configVersion) {
        return ConvertHelper.convert(serviceConfigMapper.selectOneByServiceAndConfigVersion(serviceName, configVersion),
                ServiceConfigDTO.class);
    }
}
