package io.choerodon.manager.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

/**
 * @author flyleft
 * @date 2018/4/20
 */
public class InstanceDTO {

    private String instanceId;

    private String service;

    private String version;

    private String status;

    private String pod;

    private Date registrationTime;

    @JsonIgnore
    private String params;

    public InstanceDTO(String instanceId, String service, String version, String status, String pod, Date registrationTime) {
        this.instanceId = instanceId;
        this.service = service;
        this.version = version;
        this.status = status;
        this.pod = pod;
        this.registrationTime = registrationTime;
    }

    public InstanceDTO() {
    }

    public InstanceDTO(String instanceId, String service, String version, String status, String params, String pod, Date registrationTime) {
        this.instanceId = instanceId;
        this.service = service;
        this.version = version;
        this.status = status;
        this.params = params;
        this.pod = pod;
        this.registrationTime = registrationTime;
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

    public String getPod() {
        return pod;
    }

    public void setPod(String pod) {
        this.pod = pod;
    }

    public Date getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(Date registrationTime) {
        this.registrationTime = registrationTime;
    }
}
