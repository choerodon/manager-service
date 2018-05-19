package io.choerodon.manager.domain.repository;

import java.util.List;

import io.choerodon.manager.domain.manager.entity.GroupLabelE;

/**
 * {@inheritDoc}
 *
 * @author superleader8@gmail.com
 * @data 2018/3/12
 */
public interface GroupLabelRepository {

    List<GroupLabelE> find(GroupLabelE groupLabelE);

    GroupLabelE selectById(Long id);

    /**
     * 新建对象
     *
     * @param groupLabelE 组标签对象
     * @return groupLabel
     */
    GroupLabelE insert(GroupLabelE groupLabelE);

    /**
     * 更新
     */
    GroupLabelE update(GroupLabelE gle);

    void deleteByGroupLabel(GroupLabelE groupLabelE);
}
