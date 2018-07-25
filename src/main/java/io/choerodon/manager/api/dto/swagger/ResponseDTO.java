package io.choerodon.manager.api.dto.swagger;

import io.swagger.annotations.ApiModelProperty;


/**
 * @author superlee
 */
public class ResponseDTO {

    @ApiModelProperty(value = "http状态码")
    private String httpStatus;

    private String description;

    private String body;

    public String getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(String httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
