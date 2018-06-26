package io.choerodon.manager.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.manager.api.dto.InstanceDTO;
import io.choerodon.manager.api.dto.InstanceDetailDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.Map;

/**
 * @author flyleft
 */
public interface InstanceService {


    InstanceDetailDTO query(String instanceId);

    void update(String instanceId, Long configId);

    Page<InstanceDTO> listByOptions(String service, Map<String, Object> map, PageRequest pageRequest);
}
