package io.choerodon.manager.api.dto;


import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.Map;

public class ConfigVO {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "配置名")
    private String name;
    @ApiModelProperty(value = "配置版本")
    private String configVersion;
    @ApiModelProperty(value = "是否是默认配置")
    private Boolean isDefault;

    @ApiModelProperty(value = "对应微服务ID")
    private Long serviceId;

    @ApiModelProperty(value = "配置值列表")
    private Map<String, Object> value;

    @ApiModelProperty(value = "来源（自定义/预定义）")
    private String source;

    @ApiModelProperty(value = "发布时间")
    private Date publicTime;
    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;
    @ApiModelProperty(value = "文本格式")
    private String txt;
    @ApiModelProperty(value = "修改时间")
    private Date lastUpdateDate;

    public ConfigVO(String name, String configVersion, Boolean isDefault, String source) {
        this.name = name;
        this.configVersion = configVersion;
        this.isDefault = isDefault;
        this.source = source;
    }

    public ConfigVO() {
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

    public String getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(String configVersion) {
        this.configVersion = configVersion;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Map<String, Object> getValue() {
        return value;
    }

    public void setValue(Map<String, Object> value) {
        this.value = value;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getPublicTime() {
        return publicTime;
    }

    public void setPublicTime(Date publicTime) {
        this.publicTime = publicTime;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    public String toString() {
        return "ConfigVO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", configVersion='" + configVersion + '\'' +
                ", isDefault=" + isDefault +
                ", serviceId=" + serviceId +
                ", value=" + value +
                ", source='" + source + '\'' +
                ", publicTime=" + publicTime +
                ", objectVersionNumber=" + objectVersionNumber +
                ", txt='" + txt + '\'' +
                '}';
    }
}
