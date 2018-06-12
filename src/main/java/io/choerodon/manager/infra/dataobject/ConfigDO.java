package io.choerodon.manager.infra.dataobject;

import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * @author wuguokai
 */
@VersionAudit
@ModifyAudit
@Table(name = "mgmt_service_config")
public class ConfigDO extends AuditDomain {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String configVersion;

    private Boolean isDefault;

    private Long serviceId;

    private String value;

    private String source;

    private Date publicTime;

    public ConfigDO() {
    }

    public ConfigDO(Boolean isDefault, Long serviceId) {
        this.isDefault = isDefault;
        this.serviceId = serviceId;
    }

    public ConfigDO(Long id, String name, String configVersion, Boolean isDefault, Long serviceId,
                    String value, String source, Date publicTime, Long objectVersionNumber) {
        this.id = id;
        this.name = name;
        this.configVersion = configVersion;
        this.isDefault = isDefault;
        this.serviceId = serviceId;
        this.value = value;
        this.source = source;
        this.publicTime = publicTime;
        this.setObjectVersionNumber(objectVersionNumber);
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
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


}
