package io.choerodon.manager.api.dto;

import io.swagger.annotations.ApiModelProperty;

public class ConfigCheckDTO {

    @ApiModelProperty(value = "配置名")
    private String name;

    @ApiModelProperty(value = "配置版本")
    private String configVersion;

    @ApiModelProperty(value = "服务名")
    private String serviceName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(String configVersion) {
        this.configVersion = configVersion;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
