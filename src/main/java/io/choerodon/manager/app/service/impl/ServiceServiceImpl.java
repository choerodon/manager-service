package io.choerodon.manager.app.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.springframework.data.domain.Pageable;
import io.choerodon.manager.api.dto.ServiceManagerDTO;
import io.choerodon.manager.app.service.ServiceService;
import io.choerodon.manager.infra.common.utils.ManualPageHelper;
import io.choerodon.manager.infra.dto.ServiceDTO;
import io.choerodon.manager.infra.mapper.ServiceMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * @author wuguokai
 */
@Service
public class ServiceServiceImpl implements ServiceService {


    private DiscoveryClient discoveryClient;

    private ServiceMapper serviceMapper;

    public ServiceServiceImpl(DiscoveryClient discoveryClient,
                              ServiceMapper serviceMapper) {
        this.discoveryClient = discoveryClient;
        this.serviceMapper = serviceMapper;
    }

    @Override
    public List<ServiceDTO> list(String param) {
        if (StringUtils.isEmpty(param)) {
            return serviceMapper.selectAll();
        } else {
            return serviceMapper.selectServicesByFilter(param);
        }
    }

    @Override
    public PageInfo<ServiceManagerDTO> pageManager(String serviceName, String params, Pageable pageable) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        List<ServiceManagerDTO> serviceManagers = new ArrayList<>();
        discoveryClient.getServices().forEach(t -> serviceManagers
                .add(new ServiceManagerDTO(t, discoveryClient.getInstances(t).size())));
        if (pageable.getPageSize()==0) {
            Page<ServiceManagerDTO> dtoPage = new Page<>(page, size);
            dtoPage.addAll(serviceManagers);
            return dtoPage.toPageInfo();
        } else {
            Map<String, Object> map = new HashMap<>(5);
            map.put("serviceName", serviceName);
            map.put("params", params);
            return ManualPageHelper.postPage(serviceManagers, pageable, map);
        }

    }
}
