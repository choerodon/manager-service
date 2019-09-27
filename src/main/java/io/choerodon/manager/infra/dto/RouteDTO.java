package io.choerodon.manager.infra.dto;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "FD_ROUTE")
public class RouteDTO extends BaseDTO {
    @Id
    @GeneratedValue
    private Long id;
    private String serviceCode;
    private String backendPath;
    private String frontendPath;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getBackendPath() {
        return backendPath;
    }

    public void setBackendPath(String backendPath) {
        this.backendPath = backendPath;
    }

    public String getFrontendPath() {
        return frontendPath;
    }

    public void setFrontendPath(String frontendPath) {
        this.frontendPath = frontendPath;
    }
}
