package io.choerodon.manager.domain.service;

import java.util.List;

import io.choerodon.manager.domain.manager.entity.GroupLabelE;
import io.choerodon.manager.infra.dataobject.GroupLabelDO;
import io.choerodon.mybatis.service.BaseService;

/**
 * 用户组标签业务service
 *
 * @author wuguokai
 */
public interface IGroupLabelService extends BaseService<GroupLabelDO> {
    /**
     * 更新用户组标签
     *
     * @param groupLabelEList 需要更新的信息
     * @return 更新之后的标签信息
     */
    List<GroupLabelE> update(List<GroupLabelE> groupLabelEList);

    /**
     * 删除用户组标签
     *
     * @param labelValue 删除的键值
     */
    void deleteGroupLabel(String labelValue);

    /**
     * 查询用户组标签
     *
     * @param groupLabelE 查询的条件
     * @return 用户组标签列表
     */
    List<GroupLabelE> query(GroupLabelE groupLabelE);

    GroupLabelE insert(GroupLabelE groupLabelE);

}
