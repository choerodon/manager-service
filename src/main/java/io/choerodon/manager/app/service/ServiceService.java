package io.choerodon.manager.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.manager.api.dto.ServiceDTO;
import io.choerodon.manager.api.dto.ServiceManagerDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * @author wuguokai
 */
public interface ServiceService {

    List<ServiceDTO> list(String param);

    Page<ServiceManagerDTO> pageManager(String serviceName, String params, PageRequest pageRequest);

}
