package io.choerodon.manager.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.manager.api.dto.HostDTO;

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
     * @return
     */
    PageInfo<HostDTO> pagingHosts(String sourceType, String hostName, String ipAddr, int port, String appName, String[] params);

}
