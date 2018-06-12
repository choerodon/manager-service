package io.choerodon.manager.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.manager.api.dto.ConfigDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * @author wuguokai
 */
public interface ConfigService {

    ConfigDTO queryDefaultByServiceName(String serviceName);

    ConfigDTO queryByServiceNameAndConfigVersion(String serviceName, String configVersion);

    List<ConfigDTO> listByServiceName(String serviceName);

    Page<ConfigDTO> listByServiceId(Long serviceId, PageRequest pageRequest);

    Page<ConfigDTO> list(PageRequest pageRequest);

    ConfigDTO setServiceConfigDefault(Long configId);

    ConfigDTO query(Long configId);

    Boolean delete(Long configId);

    ConfigDTO update(Long configId, ConfigDTO configDTO);

    ConfigDTO create(ConfigDTO configDTO);
}
