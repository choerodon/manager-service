package io.choerodon.manager.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.manager.api.dto.ServiceManagerDTO;
import io.choerodon.manager.infra.dto.ServiceDTO;

import java.util.List;

/**
 * @author wuguokai
 */
public interface ServiceService {

    List<ServiceDTO> list(String param);

    PageInfo<ServiceManagerDTO> pageManager(String serviceName, String params, PageRequest pageRequest);
}
