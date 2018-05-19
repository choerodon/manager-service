package io.choerodon.manager.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.GroupLabelDTO;
import io.choerodon.manager.app.service.GroupLabelService;
import io.choerodon.manager.domain.manager.entity.GroupLabelE;
import io.choerodon.manager.domain.service.IGroupLabelService;

/**
 * {@inheritDoc}
 *
 * @author superleader8@gmail.com
 * @data 2018/3/14
 */
@Component
public class GroupLabelServiceImpl implements GroupLabelService {

    @Autowired
    private IGroupLabelService service;


    public GroupLabelServiceImpl(IGroupLabelService service) {
        this.service = service;
    }

    @Override
    public List<GroupLabelDTO> query(GroupLabelDTO groupLabelDTO) {
        return ConvertHelper.convertList(service
                .query(ConvertHelper.convert(groupLabelDTO, GroupLabelE.class)), GroupLabelDTO.class);
    }

    @Override
    public List<GroupLabelDTO> update(List<GroupLabelDTO> groupLabelDTOList) {
        groupLabelDTOList.forEach(gl -> {
            if (gl.getId() == null) {
                throw new CommonException("error.groupLabel.id.isEmpty");
            }
            if (gl.getGroupCode() == null || gl.getLabelValue() == null) {
                throw new CommonException("error.groupLabel.codeOrLabel.isEmpty");
            }
        });
        return ConvertHelper.convertList(service
                .update(ConvertHelper.convertList(groupLabelDTOList, GroupLabelE.class)), GroupLabelDTO.class);
    }

    @Override
    public GroupLabelDTO insert(GroupLabelDTO groupLabelDTO) {
        if (groupLabelDTO.getGroupCode() == null) {
            throw new CommonException("error.groupCode.isEmpty");
        }
        if (groupLabelDTO.getLabelValue() == null) {
            throw new CommonException("error.groupLabel.isEmpty");
        }
        return ConvertHelper.convert(service
                .insert(ConvertHelper.convert(groupLabelDTO, GroupLabelE.class)), GroupLabelDTO.class);
    }

    @Override
    public void delete(String labelValue) {
        if (labelValue == null) {
            throw new CommonException("error.labelValue.empty");
        }
        service.deleteGroupLabel(labelValue);
    }
}
