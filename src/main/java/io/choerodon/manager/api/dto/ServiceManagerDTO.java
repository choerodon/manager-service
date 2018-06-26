package io.choerodon.manager.api.dto;

public class ServiceManagerDTO {

    private String serviceName;

    private Integer instanceNum;

    public ServiceManagerDTO(String serviceName, Integer instanceNum) {
        this.serviceName = serviceName;
        this.instanceNum = instanceNum;
    }

    public ServiceManagerDTO() {
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Integer getInstanceNum() {
        return instanceNum;
    }

    public void setInstanceNum(Integer instanceNum) {
        this.instanceNum = instanceNum;
    }

}
