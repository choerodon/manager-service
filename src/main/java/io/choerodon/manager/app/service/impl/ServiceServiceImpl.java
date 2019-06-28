package io.choerodon.manager.app.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.manager.api.dto.ServiceDTO;
import io.choerodon.manager.api.dto.ServiceManagerDTO;
import io.choerodon.manager.app.service.ServiceService;
import io.choerodon.manager.domain.repository.ServiceRepository;
import io.choerodon.manager.infra.common.utils.ManualPageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * @author wuguokai
 */
@Service
public class ServiceServiceImpl implements ServiceService {

    private ServiceRepository serviceRepository;

    private DiscoveryClient discoveryClient;

    public ServiceServiceImpl(ServiceRepository serviceRepository, DiscoveryClient discoveryClient) {
        this.serviceRepository = serviceRepository;
        this.discoveryClient = discoveryClient;
    }

    @Override
    public List<ServiceDTO> list(String param) {
        if (StringUtils.isEmpty(param)) {
            return ConvertHelper.convertList(serviceRepository.getAllService(), ServiceDTO.class);
        }
        return ConvertHelper.convertList(serviceRepository.selectServicesByFilter(param), ServiceDTO.class);
    }

    @Override
    public PageInfo<ServiceManagerDTO> pageManager(String serviceName, String params, PageRequest pageRequest) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        List<ServiceManagerDTO> serviceManagers = new ArrayList<>();
        discoveryClient.getServices().forEach(t -> serviceManagers
                .add(new ServiceManagerDTO(t, discoveryClient.getInstances(t).size())));
        if (pageRequest.isQueriedAll()) {
            Page<ServiceManagerDTO> dtoPage = new Page<>(page, size);
            dtoPage.addAll(serviceManagers);
            return dtoPage.toPageInfo();
        } else {
            Map<String, Object> map = new HashMap<>(5);
            map.put("serviceName", serviceName);
            map.put("params", params);
            return ManualPageHelper.postPage(serviceManagers, pageRequest, map);
        }

    }
}
