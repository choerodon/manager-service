package io.choerodon.manager.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.manager.api.dto.ConfigDTO;
import io.choerodon.manager.domain.manager.entity.ConfigE;
import io.choerodon.manager.infra.dataobject.ConfigDO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * {@inheritDoc}
 *
 * @author wuguokai
 */
public interface ConfigRepository {

    ConfigDTO queryDefaultByServiceName(String serviceName);

    ConfigDTO queryByServiceNameAndConfigVersion(String serviceName, String configVersion);

    Page<ConfigDTO> listByServiceId(Long serviceId, PageRequest pageRequest);

    Page<ConfigDTO> list(PageRequest pageRequest);

    ConfigDO setConfigDefault(Long configId);

    ConfigE query(Long serviceConfigId);

    boolean delete(Long configId);

    ConfigE update(Long configId, ConfigE configE);

    List<ConfigDTO> listByServiceName(String serviceName);

    ConfigDO create(ConfigDO configDO);
}
