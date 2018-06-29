package io.choerodon.manager.domain.repository;

import io.choerodon.manager.domain.manager.entity.ServiceE;
import io.choerodon.manager.infra.dataobject.ServiceDO;

import java.util.List;

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

    List<ServiceDO> getAllService();

    List<ServiceDO> selectServicesByFilter(String param);
}
