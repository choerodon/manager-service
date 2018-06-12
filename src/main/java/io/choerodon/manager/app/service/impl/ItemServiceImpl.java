package io.choerodon.manager.app.service.impl;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.ItemDto;
import io.choerodon.manager.api.dto.ConfigDTO;
import io.choerodon.manager.app.service.ItemService;
import io.choerodon.manager.app.service.ConfigService;
import io.choerodon.manager.infra.common.annotation.ConfigNotifyRefresh;
import io.choerodon.mybatis.util.StringUtil;

/**
 * 实现类
 *
 * @author wuguokai
 */
@Component
public class ItemServiceImpl implements ItemService {

    private ConfigService configService;

    public ItemServiceImpl(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    @ConfigNotifyRefresh
    public ItemDto saveItem(Long configId, ItemDto item) {
        if (item == null || StringUtil.isEmpty(item.getProperty()) || StringUtil.isEmpty(item.getValue())) {
            throw new CommonException("error.config.item.add");
        }
        ConfigDTO configDTO = preOp(configId);
        Map<String, Object> itemMap = configDTO.getValue();
        if (checkNeedUpdate(itemMap, item)) {
            itemMap.put(item.getProperty(), item.getValue());
            configDTO.setValue(itemMap);
            if (configService.update(configDTO.getId(), configDTO) == null) {
                throw new CommonException("error.config.item.add");
            }
        }
        return item;
    }

    @Override
    @ConfigNotifyRefresh
    public void deleteItem(Long configId, String property) {
        if (StringUtil.isEmpty(property)) {
            throw new CommonException("error.config.item.update");
        }
        ConfigDTO configDTO = preOp(configId);
        Map<String, Object> itemMap = configDTO.getValue();
        Set<String> keySet = itemMap.keySet();
        if (!keySet.contains(property)) {
            throw new CommonException("error.config.item.not.exist");
        }
        itemMap.remove(property);
        configService.update(configDTO.getId(), configDTO);
    }

    private ConfigDTO preOp(Long configId) {
        if (configId == null || configId < 1) {
            throw new CommonException("error.config.query");
        }
        ConfigDTO configDTO = configService.query(configId, null);
        if (configDTO == null) {
            throw new CommonException("error.config.query");
        }
        return configDTO;
    }

    private boolean checkNeedUpdate(Map<String, Object> map, ItemDto item) {
        String key = item.getProperty();
        String value = item.getValue();
        return !map.containsKey(key) || !value.equals(map.get(key));
    }
}
