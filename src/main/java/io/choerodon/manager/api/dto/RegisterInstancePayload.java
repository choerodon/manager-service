package io.choerodon.manager.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 从消息队列拿到的服务启动下线信息对应的实体
 *
 * @author zhipeng.zuo
 * @date 2018/1/23
 */
public class RegisterInstancePayload {

    private String status;

    private String appName;

    private String id;

    private String version;

    private String uuid;

    private String apiData;

    @JsonIgnore
    private AtomicInteger executeTime = new AtomicInteger(0);

    @JsonIgnore
    private AtomicBoolean swaggerStatus = new AtomicBoolean(false);

    @JsonIgnore
    private AtomicBoolean permissionStatus = new AtomicBoolean(false);

    @JsonIgnore
    private AtomicBoolean routeStatus = new AtomicBoolean(false);

    public String getApiData() {
        return apiData;
    }

    public void setApiData(String apiData) {
        this.apiData = apiData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void increaseExecuteTime() {
        this.executeTime.incrementAndGet();
    }

    public int getExecuteTime() {
        return executeTime.get();
    }

    public boolean getSwaggerStatus() {
        return swaggerStatus.get();
    }

    public void setSwaggerStatusSuccess() {
        this.swaggerStatus.set(true);
    }

    public boolean getPermissionStatus() {
        return permissionStatus.get();
    }

    public void setPermissionStatusSuccess() {
        this.permissionStatus.set(true);
    }

    public boolean getRouteStatus() {
        return routeStatus.get();
    }

    public void setRouteStatusSuccess() {
        this.routeStatus.set(true);
    }



    @Override
    public String toString() {
        return "RegisterInstancePayload{" +
                "status='" + status + '\'' +
                ", appName='" + appName + '\'' +
                ", id='" + id + '\'' +
                ", version='" + version + '\'' +
                ", uuid='" + uuid + '\'' +
                ", executeTime=" + executeTime +
                ", swaggerStatus=" + swaggerStatus +
                ", permissionStatus=" + permissionStatus +
                ", routeStatus=" + routeStatus +
                '}';
    }
}
