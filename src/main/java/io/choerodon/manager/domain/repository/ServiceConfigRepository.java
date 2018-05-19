package io.choerodon.manager.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.manager.api.dto.ServiceConfigDTO;
import io.choerodon.manager.domain.manager.entity.ServiceConfigE;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * {@inheritDoc}
 *
 * @author wuguokai
 */
public interface ServiceConfigRepository {

    ServiceConfigDTO queryDefaultByServiceName(String serviceName);

    ServiceConfigDTO queryByServiceNameAndConfigVersion(String serviceName, String configVersion);

    Page<ServiceConfigDTO> listByServiceId(Long serviceId, PageRequest pageRequest);

    Page<ServiceConfigDTO> list(PageRequest pageRequest);

    ServiceConfigE setConfigDefault(Long configId);

    ServiceConfigE query(Long serviceConfigId);

    boolean delete(Long configId);

    ServiceConfigE update(Long configId, ServiceConfigE serviceConfigE);
}
