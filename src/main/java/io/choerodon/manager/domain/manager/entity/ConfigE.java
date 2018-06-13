package io.choerodon.manager.domain.manager.entity;

import java.util.Date;

/**
 * @author superleader8@gmail.com
 * @author wuguokai
 */
public class ConfigE {
    private Long id;

    private String name;

    private String configVersion;

    private Boolean isDefault;

    private Long serviceId;

    private String value;

    private String source;

    private Date publicTime;

    private Long objectVersionNumber;

    public ConfigE(Long id, String name, String configVersion, Boolean isDefault, Long serviceId,
                   String value, String source, Date publicTime, Long objectVersionNumber) {
        this.id = id;
        this.name = name;
        this.configVersion = configVersion;
        this.isDefault = isDefault;
        this.serviceId = serviceId;
        this.value = value;
        this.source = source;
        this.publicTime = publicTime;
        this.objectVersionNumber = objectVersionNumber;
    }

    public void setIsDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getConfigVersion() {
        return configVersion;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSource() {
        return source;
    }

    public Date getPublicTime() {
        return publicTime;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }
}
