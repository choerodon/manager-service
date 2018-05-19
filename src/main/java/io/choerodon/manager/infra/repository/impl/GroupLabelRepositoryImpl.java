package io.choerodon.manager.infra.repository.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.domain.manager.entity.GroupLabelE;
import io.choerodon.manager.domain.repository.GroupLabelRepository;
import io.choerodon.manager.infra.dataobject.GroupLabelDO;
import io.choerodon.manager.infra.mapper.GroupLabelMapper;

/**
 * {@inheritDoc}
 *
 * @author superleader8@gmail.com
 * @data 2018/3/12
 */
@Component
public class GroupLabelRepositoryImpl implements GroupLabelRepository {

    private GroupLabelMapper mapper;


    public GroupLabelRepositoryImpl(GroupLabelMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<GroupLabelE> find(GroupLabelE groupLabelE) {
        return ConvertHelper.convertList(mapper
                .select(ConvertHelper.convert(groupLabelE, GroupLabelDO.class)), GroupLabelE.class);
    }

    @Override
    public GroupLabelE selectById(Long id) {
        return ConvertHelper.convert(mapper.selectByPrimaryKey(id), GroupLabelE.class);
    }

    /**
     * 新建对象
     *
     * @param groupLabelE 组标签对象
     * @return groupLabel
     */
    @Override
    public GroupLabelE insert(GroupLabelE groupLabelE) {
        GroupLabelDO groupLabelDO = ConvertHelper.convert(groupLabelE, GroupLabelDO.class);
        if (mapper.insert(groupLabelDO) != 1) {
            throw new CommonException("error.groupLabel.insert");
        }
        return ConvertHelper.convert(groupLabelDO, GroupLabelE.class);
    }

    /**
     * 更新
     */
    @Override
    public GroupLabelE update(GroupLabelE gle) {
        GroupLabelDO groupLabelDO = ConvertHelper.convert(gle, GroupLabelDO.class);
        if (mapper.updateByPrimaryKeySelective(groupLabelDO) != 1) {
            throw new CommonException("error.groupLabel.update");
        }
        return ConvertHelper.convert(groupLabelDO, GroupLabelE.class);
    }

    @Override
    public void deleteByGroupLabel(GroupLabelE groupLabelE) {
        mapper.delete(ConvertHelper.convert(groupLabelE, GroupLabelDO.class));
    }
}
