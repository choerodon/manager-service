package io.choerodon.manager.api.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author wanghao
 * @Date 2019/11/4 16:11
 */
public class HostVO {

    private static final String IP_FORMAT = "((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))";
    private static final String CHINESE_AND_ALPHANUMERIC_AND_SYMBOLS_30 = "^[A-Za-z0-9\\u4e00-\\u9fa5-_\\.]{1,30}$";

    @ApiModelProperty(value = "ip")
    @NotEmpty(message = "error.ip.empty")
    @Pattern(regexp = IP_FORMAT,message = "error.ip.format.invalid")
    private String ipAddr;

    @ApiModelProperty(value = "主机名")
    @NotEmpty(message = "error.hostName.empty")
    @Pattern(regexp = CHINESE_AND_ALPHANUMERIC_AND_SYMBOLS_30,message = "error.hostName.format.invalid")
    private String hostName;

    @ApiModelProperty(value = "端口")
    @NotNull(message = "error.port.empty")
    @Min(value = 0, message = "error.port.format.invalid")
    private Integer port;

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
