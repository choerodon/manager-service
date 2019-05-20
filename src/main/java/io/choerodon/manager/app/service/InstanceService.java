package io.choerodon.manager.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.manager.api.dto.InstanceDTO;
import io.choerodon.manager.api.dto.InstanceDetailDTO;
import io.choerodon.manager.infra.dataobject.Sort;

import java.util.Map;

/**
 * @author flyleft
 */
public interface InstanceService {


    InstanceDetailDTO query(String instanceId);

    void update(String instanceId, Long configId);

    PageInfo<InstanceDTO> listByOptions(String service, Map<String, Object> map, int page, int size, Sort sort);
}
