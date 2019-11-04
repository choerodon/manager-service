package io.choerodon.manager.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.manager.api.dto.HostDTO;
import io.choerodon.manager.api.dto.HostVO;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author wanghao
 * @Date 2019/11/4 10:17
 */
public interface HostService {
    /**
     * 分页查询主机列表
     * @param sourceType
     * @param hostName
     * @param ipAddr
     * @param port
     * @param appName
     * @param params
     * @param pageable
     * @return
     */
    PageInfo<HostDTO> pagingHosts(String sourceType, String hostName, String ipAddr, Integer port, String appName, String[] params, Pageable pageable);

    List<HostDTO> listHosts();

    void deleteHost(String appName, String instanceId);

    void saveHost(String appName, HostVO hostVO);

    void updateHost(String appName, HostVO hostVO);
}
