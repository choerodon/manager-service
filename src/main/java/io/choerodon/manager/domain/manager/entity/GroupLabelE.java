package io.choerodon.manager.domain.manager.entity;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.choerodon.manager.domain.repository.GroupLabelRepository;

/**
 * {@inheritDoc}
 *
 * @author superleader8@gmail.com
 * @data 2018/3/11
 */
@Component
@Scope("prototype")
public class GroupLabelE {

    private Long id;

    private String labelValue;

    private String groupCode;

    private Long objectVersionNumber;

    @Autowired
    private GroupLabelRepository groupLabelRepository;

    /**
     * 获取自身对象
     *
     * @return List
     */
    public List<GroupLabelE> querySelf() {
        return groupLabelRepository.find(this);
    }

    /**
     * 添加对象
     *
     * @return GroupLabelE
     */
    public GroupLabelE addSelf() {
        return groupLabelRepository.insert(this);
    }

    /**
     * 获取对象
     *
     * @param id 对象id
     * @return GroupLabelE
     */
    public GroupLabelE getById(Long id) {
        return groupLabelRepository.selectById(id);
    }

    /**
     * 更新自身对象
     *
     * @return
     */
    public GroupLabelE updateSelf() {
        return groupLabelRepository.update(this);
    }

    public void deleteSelf() {
        groupLabelRepository.deleteByGroupLabel(this);
    }

    public String getLabelValue() {
        return labelValue;
    }

    public void setLabelValue(String labelValue) {
        this.labelValue = labelValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
