package io.choerodon.manager.api.controller.v1;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.manager.api.dto.InstanceDTO;
import io.choerodon.manager.api.dto.ServiceDTO;
import io.choerodon.manager.app.service.ServiceService;
import io.choerodon.manager.infra.common.utils.DiscoveryUtil;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;

/**
 * 操作服务控制器
 *
 * @author wuguokai
 */
@RestController
@RequestMapping(value = "/v1/services")
public class ServiceController {

    private ServiceService serviceService;
    private DiscoveryUtil discoveryUtil;

    public ServiceController(ServiceService serviceService, DiscoveryUtil discoveryUtil) {
        this.serviceService = serviceService;
        this.discoveryUtil = discoveryUtil;
    }

    /**
     * 分页查询服务信息
     *
     * @return page
     */
    @Permission(level = ResourceLevel.SITE, roles = {"managerAdmin"})
    @ApiOperation("分页查询服务信息")
    @GetMapping(value = "/page")
    public ResponseEntity<Page<ServiceDTO>> pageAll(@SortDefault(value = "id", direction = Sort.Direction.ASC) PageRequest pageRequest) {
        return Optional.ofNullable(serviceService.pageAll(pageRequest))
                .map(i -> new ResponseEntity<>(i, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.service.query"));
    }


    /**
     * 查询某一个服务现有的标签
     *
     * @param serviceName 服务名
     * @return Set
     */
    @Permission(level = ResourceLevel.SITE, roles = {"managerAdmin"})
    @ApiOperation("查询某一个服务现有的标签")
    @GetMapping(value = "/{service}/labels")
    public ResponseEntity<Set<String>> queryByServiceName(@PathVariable("service") String serviceName) {
        try {
            Set<String> labelSet = discoveryUtil.getServiceLabelSet(serviceName);
            return new ResponseEntity<>(new HashSet<>(labelSet), HttpStatus.OK);
        } catch (Exception e) {
            throw new CommonException("error.label.service.query");
        }
    }

    /**
     * 查询服务实例列表
     *
     * @param service 服务名
     * @return 实例列表
     */
    @Permission(level = ResourceLevel.SITE, roles = {"managerAdmin"})
    @ApiOperation("查询服务实例列表")
    @GetMapping(value = "/{service}/instances")
    public List<InstanceDTO> list(@PathVariable("service") String service) {
        return serviceService.getInstancesByService(service);
    }
}
