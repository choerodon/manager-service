package io.choerodon.manager.domain.repository;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.manager.domain.manager.entity.ServiceE;
import io.choerodon.manager.infra.dataobject.ServiceDO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author superleader8@gmail.com
 * @author wuguokai
 */
public interface ServiceRepository {

    ServiceDO getService(String serviceName);

    ServiceE getService(Long serviceId);

    ServiceE addService(ServiceE serviceE);

    ServiceE updateService(ServiceE serviceE);

    boolean deleteService(Long serviceId);

    List<ServiceE> getAllService();

    Page<ServiceE> pageAllService(PageRequest pageRequest);
}
