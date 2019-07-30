package io.choerodon.manager.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.manager.api.dto.*;
import io.choerodon.manager.infra.dto.ConfigDTO;

/**
 * @author wuguokai
 */
public interface ConfigService {

    ConfigVO queryDefaultByServiceName(String serviceName);

    ConfigVO queryByServiceNameAndConfigVersion(String serviceName, String configVersion);

    PageInfo<ConfigVO> listByServiceName(String serviceName, PageRequest pageRequest, ConfigDTO configDTO, String queryParam);

    ConfigVO updateConfigDefault(Long configId);

    ConfigVO query(Long configId, String type);

    YamlDTO queryYaml(Long configId);

    Boolean delete(Long configId);

    ConfigVO update(Long configId, ConfigVO configVO);

    ConfigVO updateConfig(Long configId, ConfigVO configVO, String type);

    ConfigVO create(CreateConfigDTO createConfigDTO);

    void check(ConfigCheckDTO configDTO);

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
