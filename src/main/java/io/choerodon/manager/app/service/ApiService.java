package io.choerodon.manager.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.manager.api.dto.swagger.ControllerDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.Map;

/**
 * @author superlee
 */
public interface ApiService {

    Page<ControllerDTO> getControllers(String name, String version, PageRequest pageRequest, Map<String, Object> map);

    ControllerDTO queryPathDetail(String serviceName, String version, String controllerName, String operationId);
}
