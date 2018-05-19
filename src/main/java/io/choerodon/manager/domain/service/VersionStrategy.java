package io.choerodon.manager.domain.service;

/**
 * 服务版本对比工具
 *
 * @author zhipeng.zuo
 * @author wuguokai
 */
public interface VersionStrategy {

    /**
     * 对比服务版本
     *
     * @param v1 版本1
     * @param v2 版本2
     * @return int
     */
    int compareVersion(String v1, String v2);

}
