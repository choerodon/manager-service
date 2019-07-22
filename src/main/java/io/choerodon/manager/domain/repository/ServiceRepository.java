package io.choerodon.manager.domain.repository;

import io.choerodon.manager.domain.manager.entity.ServiceE;
import io.choerodon.manager.infra.dto.ServiceDTO;

import java.util.List;

/**
 * @author superleader8@gmail.com
 * @author wuguokai
 */
public interface ServiceRepository {

    ServiceDTO getService(String serviceName);

    ServiceE getService(Long serviceId);

    ServiceE addService(ServiceE serviceE);

    ServiceE updateService(ServiceE serviceE);

    boolean deleteService(Long serviceId);

    List<ServiceDTO> getAllService();

    List<ServiceDTO> selectServicesByFilter(String param);
}
