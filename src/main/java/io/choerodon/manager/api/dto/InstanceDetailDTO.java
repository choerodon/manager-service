package io.choerodon.manager.api.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Date;
import java.util.Map;

/**
 * @author superlee
 */
public class InstanceDetailDTO {

    private String instanceId;
    private String hostName;
    private String ipAddr;
    private String app;
    private String port;
    private String version;
    private Date registrationTime;
    private Map<String, String> metadata;
    private YamlDTO configInfoYml;
    private YamlDTO envInfoYml;

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

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

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(Date registrationTime) {
        this.registrationTime = registrationTime;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public YamlDTO getConfigInfoYml() {
        return configInfoYml;
    }

    public void setConfigInfoYml(YamlDTO configInfoYml) {
        this.configInfoYml = configInfoYml;
    }

    public YamlDTO getEnvInfoYml() {
        return envInfoYml;
    }

    public void setEnvInfoYml(YamlDTO envInfoYml) {
        this.envInfoYml = envInfoYml;
    }
}
