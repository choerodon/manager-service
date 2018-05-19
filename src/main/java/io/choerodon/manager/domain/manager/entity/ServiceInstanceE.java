package io.choerodon.manager.domain.manager.entity;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * {@inheritDoc}
 *
 * @author superleader8@gmail.com
 * @data 2018/3/11
 */
@Component
@Scope("prototype")
public class ServiceInstanceE {

    private String status;

    private String appName;

    private String id;

    private String version;

    private String uuid;

    private long lastUpdateTime = System.currentTimeMillis();

    private String apiData;

    public ServiceInstanceE() {
        //
    }

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

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
}
