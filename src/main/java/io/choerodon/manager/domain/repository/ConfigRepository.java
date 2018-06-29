package io.choerodon.manager.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.manager.api.dto.ConfigDTO;
import io.choerodon.manager.infra.dataobject.ConfigDO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * {@inheritDoc}
 *
 * @author wuguokai
 */
public interface ConfigRepository {

    ConfigDTO queryDefaultByServiceName(String serviceName);

    ConfigDTO queryByServiceNameAndConfigVersion(String serviceName, String configVersion);

    Page<ConfigDTO> listByServiceName(String serviceName, PageRequest pageRequest, ConfigDO queryInfo, String queryParam);

    Page<ConfigDTO> list(PageRequest pageRequest);

    ConfigDO setConfigDefault(Long configId);

    ConfigDO query(Long serviceConfigId);

    boolean delete(Long configId);

    ConfigDO update(Long configId, ConfigDO configDO);

    ConfigDO create(ConfigDO configDO);

    ConfigDO queryByServiceIdAndVersion(Long serviceId, String configVersion);

    ConfigDO queryByServiceIdAndName(Long serviceId, String name);
}
