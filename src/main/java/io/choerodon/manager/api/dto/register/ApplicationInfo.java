package io.choerodon.manager.api.dto.register;

import com.netflix.discovery.shared.Applications;

/**
 * @author wanghao
 * @Date 2019/11/4 11:46
 */
public class ApplicationInfo {
    private Applications applications;

    public Applications getApplications() {
        return applications;
    }

    public void setApplications(Applications applications) {
        this.applications = applications;
    }
}
