package io.choerodon.manager.domain.manager.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.UiConfiguration;

/**
 * @author superleader8@gmail.com
 * @data 2018/3/11
 */
@Component
@Scope("prototype")
public class SwaggerE {

    private Long id;

    private String serviceName;

    private String serviceVersion;

    private Boolean isDefault;

    private String value;

    @Value("${choerodon.swagger.client:client}")
    private String client;

    @Value("${choerodon.swagger.skip.service}")
    private String[] skipService;

    public SecurityConfiguration getSecurityConfiguration() {
        return new SecurityConfiguration(
                client, "unknown", "default",
                "default", "token",
                ApiKeyVehicle.HEADER, "token", ",");
    }

    public UiConfiguration getUiConfiguration() {
        return new UiConfiguration(null);
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
