package io.choerodon.manager.api.dto;

/**
 * @author wanghao
 * @Date 2019/11/1 17:25
 */
public class HostDTO {

    private String hostName;

    private String ipAddr;

    private String appName;

    private String instanceId;

    private int port;

    private String sourceType;
    private String routeRuleCode;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getRouteRuleCode() {
        return routeRuleCode;
    }

    public void setRouteRuleCode(String routeRuleCode) {
        this.routeRuleCode = routeRuleCode;
    }
}
