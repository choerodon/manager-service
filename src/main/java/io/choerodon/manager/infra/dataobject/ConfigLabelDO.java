package io.choerodon.manager.infra.dataobject;

import javax.persistence.Id;

import io.choerodon.mybatis.domain.AuditDomain;

//@Table(name = "config_label")
public class ConfigLabelDO extends AuditDomain {
    @Id
    private Long id;

    private Long configId;

    private String label;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
