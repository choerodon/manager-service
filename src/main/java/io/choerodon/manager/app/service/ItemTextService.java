package io.choerodon.manager.app.service;

/**
 * 配置文本信息操作业务service
 *
 * @author wuguokai
 */
public interface ItemTextService {
    /**
     * 获取配置信息文本形式
     *
     * @param configId 配置id
     * @param type     文本类型
     * @return String
     */
    String getConfigText(Long configId, String type);

    /**
     * 使用配置文本信息更新配置
     *
     * @param configId   配置id
     * @param type       文本类型
     * @param configText 配置文本信息
     */
    void updateConfigText(Long configId, String type, String configText);
}
