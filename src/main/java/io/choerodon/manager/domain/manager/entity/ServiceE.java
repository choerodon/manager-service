package io.choerodon.manager.domain.manager.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.choerodon.manager.domain.repository.ServiceRepository;

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

    @Autowired
    private ServiceRepository serviceRepository;

    public ServiceE() {
        //
    }

    /**
     * 获取自身对象
     *
     * @return ServiceE
     */
    public ServiceE getSelf() {
        return serviceRepository.getService(this.id);
    }

    /**
     * 添加一个Service
     *
     * @return ServiceE
     */
    public ServiceE addService() {
        return serviceRepository.addService(this);
    }

    /**
     * 更新一个Service
     *
     * @return ServiceE
     */
    public ServiceE updateService() {
        return serviceRepository.updateService(this);
    }

    /**
     * 删除一个service
     *
     * @return Boolean
     */
    public boolean deleteService() {
        return serviceRepository.deleteService(this.id);
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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
