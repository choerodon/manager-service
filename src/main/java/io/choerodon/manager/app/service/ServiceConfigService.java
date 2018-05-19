package io.choerodon.manager.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.manager.api.dto.ServiceConfigDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author wuguokai
 */
public interface ServiceConfigService {

    ServiceConfigDTO queryDefaultByServiceName(String serviceName);

    ServiceConfigDTO queryByServiceNameAndConfigVersion(String serviceName, String configVersion);

    Page<ServiceConfigDTO> listByServiceId(Long serviceId, PageRequest pageRequest);

    Page<ServiceConfigDTO> list(PageRequest pageRequest);

    ServiceConfigDTO setServiceConfigDefault(Long configId);

    ServiceConfigDTO query(Long configId);

    Boolean delete(Long configId);

    ServiceConfigDTO update(Long configId, ServiceConfigDTO serviceConfigDTO);
}
