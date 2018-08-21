package io.choerodon.manager.api.dto;

import io.swagger.annotations.ApiModelProperty;

public class ServiceManagerDTO {

    @ApiModelProperty(value = "服务名")
    private String serviceName;

    @ApiModelProperty(value = "实例数量")
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
