package io.choerodon.manager.app.service;

import io.choerodon.manager.api.dto.ItemDto;

/**
 * 配置项操作业务service
 *
 * @author wuguokai
 */
public interface ItemService {
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
