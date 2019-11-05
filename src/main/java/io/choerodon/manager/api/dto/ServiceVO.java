package io.choerodon.manager.api.dto;

import java.util.List;

/**
 * @author wanghao
 * @Date 2019/11/5 16:01
 */
public class ServiceVO {
    private String appName;
    private List<HostDTO> hosts;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public List<HostDTO> getHosts() {
        return hosts;
    }

    public void setHosts(List<HostDTO> hosts) {
        this.hosts = hosts;
    }
}
