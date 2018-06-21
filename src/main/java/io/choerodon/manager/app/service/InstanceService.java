package io.choerodon.manager.app.service;

import java.util.List;


import io.choerodon.manager.api.dto.InstanceDTO;
import io.choerodon.manager.api.dto.InstanceDetailDTO;

/**
 * @author flyleft
 * @date 2018/4/20
 */
public interface InstanceService {

    List<InstanceDTO> list(String service);

    InstanceDetailDTO query(String instanceId);

    void update(String instanceId, Long configId);

}
