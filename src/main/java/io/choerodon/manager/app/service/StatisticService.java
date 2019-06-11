package io.choerodon.manager.app.service;

import io.choerodon.manager.api.dto.MenuClickDTO;

import java.util.List;
import java.util.Map;

/**
 * @author superlee
 */
public interface StatisticService {
    void saveMenuClick(List<MenuClickDTO> menuClickList);

    Map<String, Object> queryMenuClick(String beginDate, String endDate, String code);
}
