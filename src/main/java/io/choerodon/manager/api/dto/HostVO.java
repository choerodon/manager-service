package io.choerodon.manager.api.dto;

/**
 * @author wanghao
 * @Date 2019/11/4 16:11
 */
public class HostVO {

    private String ipAddr;

    private String hostName;

    private int port;

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
