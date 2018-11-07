package io.choerodon.manager.domain.manager.entity;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author superleader8@gmail.com
 * @author wuguokai
 */
@Component
@Scope("prototype")
public class ServiceE {

    private Long id;

    private String name;

    private Long objectVersionNumber;

    public ServiceE() {}

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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
