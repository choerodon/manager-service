package io.choerodon.manager.api.dto;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * 前端交互路由对象
 *
 * @author wuguokai
 */
public class RouteDTO {

    private Long id;

    @NotEmpty(message = "name不可为空")
    private String name;

    @NotEmpty(message = "path不可为空")
    private String path;

    @NotEmpty(message = "serviceId不可为空")
    private String serviceId;

    private String url;

    private Boolean stripPrefix;

    private Boolean retryable;

    private String sensitiveHeaders;

    private Boolean customSensitiveHeaders;

    private String helperService;

    private Long objectVersionNumber;

    private Boolean builtIn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getStripPrefix() {
        return stripPrefix;
    }

    public void setStripPrefix(Boolean stripPrefix) {
        this.stripPrefix = stripPrefix;
    }

    public Boolean getRetryable() {
        return retryable;
    }

    public void setRetryable(Boolean retryable) {
        this.retryable = retryable;
    }

    public String getSensitiveHeaders() {
        return sensitiveHeaders;
    }

    public void setSensitiveHeaders(String sensitiveHeaders) {
        this.sensitiveHeaders = sensitiveHeaders;
    }

    public Boolean getCustomSensitiveHeaders() {
        return customSensitiveHeaders;
    }

    public void setCustomSensitiveHeaders(Boolean customSensitiveHeaders) {
        this.customSensitiveHeaders = customSensitiveHeaders;
    }

    public String getHelperService() {
        return helperService;
    }

    public void setHelperService(String helperService) {
        this.helperService = helperService;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Boolean getBuiltIn() {
        return builtIn;
    }

    public void setBuiltIn(Boolean builtIn) {
        this.builtIn = builtIn;
    }
}
