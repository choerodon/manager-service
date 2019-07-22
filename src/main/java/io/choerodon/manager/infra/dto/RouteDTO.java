package io.choerodon.manager.infra.dto;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

import io.choerodon.mybatis.entity.BaseDTO;
import io.swagger.annotations.ApiModelProperty;

@Table(name = "mgmt_route")
public class RouteDTO extends BaseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "is_built_in")
    @ApiModelProperty(value = "是否内置")
    private Boolean builtIn;

    public String getHelperService() {
        return helperService;
    }

    public void setHelperService(String helperService) {
        this.helperService = helperService;
    }

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

    public Boolean getBuiltIn() {
        return builtIn;
    }

    public void setBuiltIn(Boolean builtIn) {
        this.builtIn = builtIn;
    }

    public RouteDTO(String name) {
        this.name = name;
    }

    public RouteDTO() {
    }

    public RouteDTO(String name, String path) {
        this.name = name;
        this.path = path;
    }
}
