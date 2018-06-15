package io.choerodon.manager.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author flyleft
 * @date 2018/4/20
 */
public class InstanceDTO {

    private String instanceId;

    private String service;

    private String version;

    private String status;

    @JsonIgnore
    private String params;

    public InstanceDTO(String instanceId, String service, String version, String status) {
        this.instanceId = instanceId;
        this.service = service;
        this.version = version;
        this.status = status;
    }

    public InstanceDTO() {
    }

    public InstanceDTO(String instanceId, String service, String version, String status, String params) {
        this.instanceId = instanceId;
        this.service = service;
        this.version = version;
        this.status = status;
        this.params = params;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
