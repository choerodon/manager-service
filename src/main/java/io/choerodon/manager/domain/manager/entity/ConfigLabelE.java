package io.choerodon.manager.domain.manager.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.choerodon.manager.domain.repository.ConfigLabelRepository;

/**
 * config的领域对象
 *
 * @author superleader8@gmail.com
 * @author wuguokai
 */
@Component
@Scope("prototype")
public class ConfigLabelE {

    private Long id;

    private Long configId;

    private String label;

    private Long objectVersionNumber;

    @Autowired
    private ConfigLabelRepository configLabelRepository;

    public ConfigLabelE() {
    }

    public ConfigLabelE(Long configId, String label) {
        this.configId = configId;
        this.label = label;
    }

    public ConfigLabelE addConfigLabel(ConfigLabelE configLabelE) {
        return configLabelRepository.addConfigLabel(configLabelE);
    }

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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
