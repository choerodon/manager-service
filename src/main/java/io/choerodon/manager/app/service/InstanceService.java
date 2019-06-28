package io.choerodon.manager.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.manager.api.dto.InstanceDTO;
import io.choerodon.manager.api.dto.InstanceDetailDTO;

import java.util.Map;

/**
 * @author flyleft
 */
public interface InstanceService {


    InstanceDetailDTO query(String instanceId);

    void update(String instanceId, Long configId);

    PageInfo<InstanceDTO> listByOptions(String service, Map<String, Object> map, PageRequest pageRequest);
}
