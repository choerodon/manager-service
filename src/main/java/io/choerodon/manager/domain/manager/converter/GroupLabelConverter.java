package io.choerodon.manager.domain.manager.converter;

import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.manager.api.dto.GroupLabelDTO;
import io.choerodon.manager.domain.factory.GroupLabelEFactory;
import io.choerodon.manager.domain.manager.entity.GroupLabelE;
import io.choerodon.manager.infra.dataobject.GroupLabelDO;

/**
 * {@inheritDoc}
 *
 * @author superleader8@gmail.com
 * @data 2018/3/11
 */
@Component
public class GroupLabelConverter implements ConvertorI<GroupLabelE, GroupLabelDO, GroupLabelDTO> {

    @Override
    public GroupLabelE doToEntity(GroupLabelDO dataObject) {
        GroupLabelE gle = GroupLabelEFactory.createGroupLabelE();
        gle.setGroupCode(dataObject.getGroupCode());
        gle.setId(dataObject.getId());
        gle.setLabelValue(dataObject.getLabelValue());
        gle.setObjectVersionNumber(dataObject.getObjectVersionNumber());
        return gle;
    }

    @Override
    public GroupLabelDO entityToDo(GroupLabelE entity) {
        GroupLabelDO groupLabelDO = new GroupLabelDO();
        groupLabelDO.setGroupCode(entity.getGroupCode());
        groupLabelDO.setId(entity.getId());
        groupLabelDO.setLabelValue(entity.getLabelValue());
        groupLabelDO.setObjectVersionNumber(entity.getObjectVersionNumber());
        return groupLabelDO;
    }

    @Override
    public GroupLabelE dtoToEntity(GroupLabelDTO groupLabelDTO) {
        GroupLabelE groupLabelE = GroupLabelEFactory.createGroupLabelE();
        groupLabelE.setId(groupLabelDTO.getId());
        groupLabelE.setGroupCode(groupLabelDTO.getGroupCode());
        groupLabelE.setLabelValue(groupLabelDTO.getLabelValue());
        groupLabelE.setObjectVersionNumber(groupLabelDTO.getObjectVersionNumber());
        return groupLabelE;
    }

    @Override
    public GroupLabelDTO entityToDto(GroupLabelE groupLabelE) {
        GroupLabelDTO groupLabelDTO = new GroupLabelDTO();
        groupLabelDTO.setId(groupLabelE.getId());
        groupLabelDTO.setGroupCode(groupLabelE.getGroupCode());
        groupLabelDTO.setLabelValue(groupLabelE.getLabelValue());
        groupLabelDTO.setObjectVersionNumber(groupLabelE.getObjectVersionNumber());
        return groupLabelDTO;
    }
}
