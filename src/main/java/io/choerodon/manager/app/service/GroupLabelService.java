package io.choerodon.manager.app.service;

import java.util.List;

import io.choerodon.manager.api.dto.GroupLabelDTO;

/**
 * {@inheritDoc}
 *
 * @author superleader8@gmail.com
 * @data 2018/3/14
 */
public interface GroupLabelService {

    List<GroupLabelDTO> query(GroupLabelDTO groupLabelDTO);

    List<GroupLabelDTO> update(List<GroupLabelDTO> groupLabelDTOList);

    GroupLabelDTO insert(GroupLabelDTO groupLabelDTO);

    void delete(String labelValue);
}
