package io.choerodon.manager.app.service;

import java.util.List;

import com.netflix.appinfo.InstanceInfo;

import io.choerodon.manager.api.dto.InstanceDTO;

/**
 * @author flyleft
 * @date 2018/4/20
 */
public interface InstanceService {

    List<InstanceDTO> list(String service);

    InstanceInfo query(String instanceId);

    void update(String instanceId, String configVersion);

}
