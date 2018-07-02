package io.choerodon.manager.app.service.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.manager.api.dto.ServiceDTO;
import io.choerodon.manager.api.dto.ServiceManagerDTO;
import io.choerodon.manager.app.service.ServiceService;
import io.choerodon.manager.domain.repository.ServiceRepository;
import io.choerodon.manager.infra.common.utils.ManualPageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author wuguokai
 */
@Service
public class ServiceServiceImpl implements ServiceService {

    private ServiceRepository serviceRepository;

    private DiscoveryClient discoveryClient;

    @Value("${choerodon.swagger.skip.service}")
    private String[] skipService;

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
    public Page<ServiceManagerDTO> pageManager(String serviceName, String params, PageRequest pageRequest) {
        List<ServiceManagerDTO> serviceManagerDTOS = new ArrayList<>();
        discoveryClient.getServices().forEach(t -> serviceManagerDTOS
                .add(new ServiceManagerDTO(t, discoveryClient.getInstances(t).size())));

        Map<String, Object> map = new HashMap<>();
        map.put("serviceName", serviceName);
        map.put("params", params);
        return ManualPageHelper.postPage(serviceManagerDTOS, pageRequest, map);
    }

    @Override
    public List<String> queryServiceExceptSkipped() {
        List<String> serviceId = discoveryClient.getServices();
        List<String> skipServices = Arrays.asList(skipService);
        List<String> intersection = serviceId.stream().filter(skipServices::contains).collect(Collectors.toList());
        List<String> returnServices = serviceId.stream().filter(item ->
                !intersection.contains(item)).collect(Collectors.toList());
        return returnServices;
    }
}
