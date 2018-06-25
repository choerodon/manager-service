package io.choerodon.manager.app.service;

import io.choerodon.manager.api.dto.ServiceDTO;

import java.util.List;

/**
 * @author wuguokai
 */
public interface ServiceService {

    List<ServiceDTO> list(String param);

}
