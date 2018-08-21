package io.choerodon.manager.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author wuguokai
 */
public class ServiceDTO {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "服务名")
    private String name;

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
}

