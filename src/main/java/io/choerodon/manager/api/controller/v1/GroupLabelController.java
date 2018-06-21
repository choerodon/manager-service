package io.choerodon.manager.api.controller.v1;

import java.util.List;
import java.util.Optional;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.manager.api.dto.GroupLabelDTO;
import io.choerodon.manager.app.service.GroupLabelService;
import io.choerodon.swagger.annotation.Permission;

/**
 * 操作用户组标签控制器
 *
 * @author wuguokai
 * @author superleader8@gmail.com
 */
@RequestMapping(value = "/v1/groupLabels")
@RestController
public class GroupLabelController {

    private GroupLabelService groupLabelService;

    public GroupLabelController(GroupLabelService groupLabelService) {
        this.groupLabelService = groupLabelService;
    }

    /**
     * 根据groupLabelDTO对应字段查询
     *
     * @param groupLabelDTO 标签名
     * @return 用户组列表
     */
    @Permission(level = ResourceLevel.SITE, permissionPublic = true)
    @ApiOperation("可以查询对应labelValue的用户组id列表")
    @GetMapping
    public ResponseEntity<List<GroupLabelDTO>> query(GroupLabelDTO groupLabelDTO) {
        return Optional.ofNullable(groupLabelService.query(groupLabelDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.groupLabel.query"));
    }

    /**
     * 更新用户组标签
     *
     * @param groupLabelDTOList 用户组标签对象
     * @return null
     */
    @Permission(level = ResourceLevel.SITE, permissionPublic = true)
    @ApiOperation("更新对应用户组")
    @PutMapping
    public ResponseEntity<List<GroupLabelDTO>> update(@RequestBody List<GroupLabelDTO> groupLabelDTOList) {
        return Optional.ofNullable(groupLabelService.update(groupLabelDTOList))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.groupLabel.update"));
    }

    /**
     * 新增用户组标签
     *
     * @param groupLabelDTO 用户组标签对象
     * @return null
     */
    @Permission(level = ResourceLevel.SITE, permissionPublic = true)
    @ApiOperation("插入对应用户组标签")
    @PostMapping
    public ResponseEntity<GroupLabelDTO> creat(@RequestBody GroupLabelDTO groupLabelDTO) {
        return Optional.ofNullable(groupLabelService.insert(groupLabelDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.groupLabel.create"));
    }


    /**
     * 删除对应用户组
     *
     * @param labelValue 标签名
     * @return null
     */
    @Permission(level = ResourceLevel.SITE, permissionPublic = true)
    @ApiOperation("删除对应用户组")
    @DeleteMapping
    public ResponseEntity delete(@RequestParam(value = "labelValue") String labelValue) {
        groupLabelService.delete(labelValue);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

