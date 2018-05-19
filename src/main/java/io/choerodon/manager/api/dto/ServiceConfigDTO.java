package io.choerodon.manager.api.dto;

import java.util.Date;
import java.util.Map;

public class ServiceConfigDTO {

    public static final String DEFAULT_VERSION = "default";

    private Long id;

    private String name;

    private String configVersion;

    private Boolean isDefault;

    private Long serviceId;

    private Map<String, Object> value;

    private String source;

    private Date publicTime;

    private Long objectVersionNumber;

    public ServiceConfigDTO() {
    }

    public ServiceConfigDTO(Long id, String name, String configVersion, Boolean isDefault, Long serviceId,
                            Map<String, Object> value, String source, Date publicTime, Long objectVersionNumber) {
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

    public static String getDefaultVersion() {
        return DEFAULT_VERSION;
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

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
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

    @Override
    public String toString() {
        return "ServiceConfigDTO{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", configVersion='" + configVersion + '\''
                + ", isDefault=" + isDefault
                + ", serviceId=" + serviceId
                + ", value=" + value
                + ", source='" + source + '\''
                + ", publicTime=" + publicTime
                + ", objectVersionNumber=" + objectVersionNumber
                + '}';
    }
}
