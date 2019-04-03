package io.choerodon.manager.api.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class CreateConfigDTO {

    @ApiModelProperty("配置名/必填")
    @NotEmpty(message = "error.config.name.empty")
    private String name;

    @ApiModelProperty("版本号/必填")
    @NotEmpty(message = "error.config.version.empty")
    private String version;

    @ApiModelProperty("服务名/必填")
    @NotEmpty(message = "error.config.serviceName.empty")
    private String serviceName;

    @ApiModelProperty("yml格式的配置信息/必填")
    @NotNull(message = "error.config.yaml.null")
    private String yaml;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYaml() {
        return yaml;
    }

    public void setYaml(String yaml) {
        this.yaml = yaml;
    }
}
