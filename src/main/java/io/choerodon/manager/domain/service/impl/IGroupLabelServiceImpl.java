package io.choerodon.manager.domain.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.domain.factory.GroupLabelEFactory;
import io.choerodon.manager.domain.manager.entity.GroupLabelE;
import io.choerodon.manager.domain.service.IGroupLabelService;
import io.choerodon.manager.infra.dataobject.GroupLabelDO;
import io.choerodon.mybatis.service.BaseServiceImpl;

/**
 * 实现类
 *
 * @author wuguokai
 * @author superleader8@gmail.com
 */
@Service
public class IGroupLabelServiceImpl extends BaseServiceImpl<GroupLabelDO> implements IGroupLabelService {
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<GroupLabelE> update(List<GroupLabelE> groupLabelEList) {
        List<GroupLabelE> groupLabelEs = new ArrayList<>();
        groupLabelEList.forEach(gle -> groupLabelEs.add(gle.updateSelf()));
        return groupLabelEs;
    }

    @Override
    public void deleteGroupLabel(String labelValue) {
        GroupLabelE gle = GroupLabelEFactory.createGroupLabelE();
        gle.setLabelValue(labelValue);
        gle.deleteSelf();
    }

    @Override
    public List<GroupLabelE> query(GroupLabelE groupLabelE) {
        return groupLabelE.querySelf();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public GroupLabelE insert(GroupLabelE groupLabelE) {
        if (!groupLabelE.querySelf().isEmpty()) {
            throw new CommonException("error.groupLabel.exist");
        }
        return groupLabelE.addSelf();
    }
}
