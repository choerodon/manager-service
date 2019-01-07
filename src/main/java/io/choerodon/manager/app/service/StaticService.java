package io.choerodon.manager.app.service;

import io.choerodon.manager.api.dto.MenuClickDTO;

import java.util.List;

/**
 * @author superlee
 */
public interface StaticService {
    void saveMenuClick(List<MenuClickDTO> menuClickList);
}
