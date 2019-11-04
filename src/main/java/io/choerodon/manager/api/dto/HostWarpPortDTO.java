package io.choerodon.manager.api.dto;

import java.util.Map;

/**
 * @author wanghao
 * @Date 2019/11/4 16:11
 */
public class HostWarpPortDTO {

    private String ipAddr;

    private String status;

    private Map<String,String> metadata;

    private Map<String,Object> port;

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public Map<String, Object> getPort() {
        return port;
    }

    public void setPort(Map<String, Object> port) {
        this.port = port;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
