package io.choerodon.manager.domain.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.manager.domain.manager.entity.ServiceE;
import io.choerodon.manager.domain.repository.ServiceRepository;
import io.choerodon.manager.domain.service.IServiceService;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 实现类
 *
 * @author wuguokai
 */
@org.springframework.stereotype.Service
public class IServiceServiceImpl implements IServiceService {

    private ServiceRepository serviceRepository;

    public IServiceServiceImpl(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public Page<ServiceE> pageAll(PageRequest pageRequest) {
        return serviceRepository.pageAllService(pageRequest);
    }
}
