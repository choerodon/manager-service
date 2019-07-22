package io.choerodon.manager.domain.repository;

import com.github.pagehelper.PageInfo;
import io.choerodon.manager.api.dto.ConfigVO;
import io.choerodon.manager.infra.dto.ConfigDTO;

/**
 * {@inheritDoc}
 *
 * @author wuguokai
 */
public interface ConfigRepository {

    ConfigVO queryDefaultByServiceName(String serviceName);

    ConfigVO queryByServiceNameAndConfigVersion(String serviceName, String configVersion);

    PageInfo<ConfigVO> listByServiceName(String serviceName, int page, int size, ConfigDTO queryInfo, String queryParam);

    ConfigDTO setConfigDefault(Long configId);

    ConfigDTO query(Long serviceConfigId);

    boolean delete(Long configId);

    ConfigDTO update(Long configId, ConfigDTO configDTO);

    ConfigDTO create(ConfigDTO configDTO);

    ConfigDTO queryByServiceIdAndVersion(Long serviceId, String configVersion);

    ConfigDTO queryByServiceIdAndName(Long serviceId, String name);
}
