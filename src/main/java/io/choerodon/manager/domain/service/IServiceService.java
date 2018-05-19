package io.choerodon.manager.domain.service;

import io.choerodon.core.domain.Page;
import io.choerodon.manager.domain.manager.entity.ServiceE;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 服务信息操作业务service
 *
 * @author wuguokai
 */
public interface IServiceService {
    /**
     * 分页获取服务列表
     *
     * @return page
     */
    Page<ServiceE> pageAll(PageRequest pageRequest);
}
