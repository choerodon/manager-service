package io.choerodon.manager.app.service;

import io.choerodon.manager.api.dto.swagger.ControllerDTO;

import java.util.List;

/**
 * @author superlee
 */
public interface ApiService {
    List<ControllerDTO> getControllers(String name, String version);
}
