package io.choerodon.manager.api.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 前端交互路由对象
 *
 * @author wuguokai
 */
public class RouteDTO {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "名称/必填")
    @NotEmpty(message = "name不可为空")
    private String name;

    @ApiModelProperty(value = "路径/必填")
    @NotEmpty(message = "path不可为空")
    private String path;

    @ApiModelProperty(value = "服务ID/必填")
    @NotEmpty(message = "serviceId不可为空")
    private String serviceId;

    @ApiModelProperty(hidden = true)
    private String url;

    @ApiModelProperty(value = "是否去除前缀/非必填")
    private Boolean stripPrefix;

    @ApiModelProperty(value = "是否重试/非必填")
    private Boolean retryable;

    @ApiModelProperty(value = "是否过滤敏感头信息/非必填")
    private String sensitiveHeaders;

    @ApiModelProperty(value = "敏感头信息/非必填")
    private Boolean customSensitiveHeaders;

    @ApiModelProperty(value = "Helper服务名")
    private String helperService;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "是否内置")
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
