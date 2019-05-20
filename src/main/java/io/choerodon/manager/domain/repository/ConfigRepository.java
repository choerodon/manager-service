package io.choerodon.manager.domain.repository;

import com.github.pagehelper.PageInfo;
import io.choerodon.manager.api.dto.ConfigDTO;
import io.choerodon.manager.infra.dataobject.ConfigDO;

/**
 * {@inheritDoc}
 *
 * @author wuguokai
 */
public interface ConfigRepository {

    ConfigDTO queryDefaultByServiceName(String serviceName);

    ConfigDTO queryByServiceNameAndConfigVersion(String serviceName, String configVersion);

    PageInfo<ConfigDTO> listByServiceName(String serviceName, int page, int size, ConfigDO queryInfo, String queryParam);

    ConfigDO setConfigDefault(Long configId);

    ConfigDO query(Long serviceConfigId);

    boolean delete(Long configId);

    ConfigDO update(Long configId, ConfigDO configDO);

    ConfigDO create(ConfigDO configDO);

    ConfigDO queryByServiceIdAndVersion(Long serviceId, String configVersion);

    ConfigDO queryByServiceIdAndName(Long serviceId, String name);
}
