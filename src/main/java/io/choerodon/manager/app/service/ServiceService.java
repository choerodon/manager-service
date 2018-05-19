package io.choerodon.manager.app.service;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.manager.api.dto.InstanceDTO;
import io.choerodon.manager.api.dto.ServiceDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author wuguokai
 */
public interface ServiceService {

    Page<ServiceDTO> pageAll(PageRequest pageRequest);

    List<InstanceDTO> getInstancesByService(String service);

}
