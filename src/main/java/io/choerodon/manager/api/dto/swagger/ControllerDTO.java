package io.choerodon.manager.api.dto.swagger;

import io.swagger.annotations.ApiModelProperty;

import java.util.Set;

/**
 * @author superlee
 */
public class ControllerDTO {

    @ApiModelProperty(value = "controller的名字")
    private String name;
    @ApiModelProperty(value = "controller的描述")
    private String description;
    @ApiModelProperty(value = "controller下的方法集")
    private Set<PathDTO> paths;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<PathDTO> getPaths() {
        return paths;
    }

    public void setPaths(Set<PathDTO> paths) {
        this.paths = paths;
    }
}
