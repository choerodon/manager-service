package io.choerodon.manager.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.manager.api.dto.ConfigDTO;
import io.choerodon.manager.api.dto.ItemDto;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * @author wuguokai
 */
public interface ConfigService {

    ConfigDTO queryDefaultByServiceName(String serviceName);

    ConfigDTO queryByServiceNameAndConfigVersion(String serviceName, String configVersion);

    Page<ConfigDTO> listByServiceName(String serviceName, PageRequest pageRequest, ConfigDTO queryInfo, String queryParam);

    ConfigDTO setServiceConfigDefault(Long configId);

    ConfigDTO query(Long configId, String type);

    Boolean delete(Long configId);

    ConfigDTO update(Long configId, ConfigDTO configDTO);

    ConfigDTO updateConfig(Long configId, ConfigDTO configDTO, String type);

    ConfigDTO create(ConfigDTO configDTO);

    /**
     * 保存配置项信息到指定的config
     *
     * @param configId 配置id
     * @param item     配置项信息对象
     * @return ItemDto
     */
    ItemDto saveItem(Long configId, ItemDto item);

    /**
     * 删除配置项信息
     *
     * @param configId 配置id
     * @param property 删除的键值
     */
    void deleteItem(Long configId, String property);
}
