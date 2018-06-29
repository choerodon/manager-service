package io.choerodon.manager.api.dto;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class CreateConfigDTO {

    @NotEmpty(message = "error.config.name.empty")
    private String name;

    @NotEmpty(message = "error.config.version.empty")
    private String version;

    @NotEmpty(message = "error.config.serviceName.empty")
    private String serviceName;

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
