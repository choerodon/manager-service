package io.choerodon.manager.app.service.impl;

import com.netflix.appinfo.InstanceInfo;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.manager.api.dto.InstanceDTO;
import io.choerodon.manager.api.dto.ServiceDTO;
import io.choerodon.manager.app.service.ServiceService;
import io.choerodon.manager.domain.repository.ServiceRepository;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.choerodon.manager.infra.common.utils.VersionUtil.METADATA_VERSION;

/**
 * @author wuguokai
 */
@Component
public class ServiceServiceImpl implements ServiceService {

    private DiscoveryClient discoveryClient;

    private ServiceRepository serviceRepository;

    public ServiceServiceImpl(DiscoveryClient discoveryClient, ServiceRepository serviceRepository) {
        this.discoveryClient = discoveryClient;
        this.serviceRepository = serviceRepository;
    }

    @Override
    public List<ServiceDTO> list(String param) {
        if (StringUtils.isEmpty(param)) {
            return ConvertHelper.convertList(serviceRepository.getAllService(), ServiceDTO.class);
        }
        return ConvertHelper.convertList(serviceRepository.selectServicesByFilter(param), ServiceDTO.class);
    }

    @Override
    public Page<InstanceDTO> listByServiceName(final InstanceDTO queryInfo, final PageRequest pageRequest) {
        Page<InstanceDTO> page = new Page<>();
        page.setSize(pageRequest.getSize());
        page.setNumber(pageRequest.getPage());
        List<InstanceDTO> serviceInstances = toInstanceDTOList(discoveryClient.getInstances(queryInfo.getService()));
        List<InstanceDTO> instanceDTOS = filter(queryInfo, serviceInstances);
        List<InstanceDTO> pageContent = getListPage(pageRequest.getPage(), pageRequest.getSize(), instanceDTOS);
        int pageSize = instanceDTOS.size() / pageRequest.getSize() + (instanceDTOS.size() % pageRequest.getSize() > 0 ? 1 : 0);
        page.setTotalPages(pageSize);
        page.setTotalElements(instanceDTOS.size());
        page.setNumberOfElements(pageContent.size());
        page.setContent(pageContent);
        return page;
    }


    private List<InstanceDTO> toInstanceDTOList(final List<ServiceInstance> serviceInstances) {
        List<InstanceDTO> instanceInfoList = new ArrayList<>();
        for (ServiceInstance serviceInstance : serviceInstances) {
            EurekaDiscoveryClient.EurekaServiceInstance eurekaServiceInstance =
                    (EurekaDiscoveryClient.EurekaServiceInstance) serviceInstance;
            InstanceInfo info = eurekaServiceInstance.getInstanceInfo();
            instanceInfoList.add(new InstanceDTO(info.getInstanceId(), info.getAppName(),
                    info.getMetadata().get(METADATA_VERSION), info.getStatus().name()));
        }
        return instanceInfoList;
    }

    private List<InstanceDTO> filter(final InstanceDTO queryInfo, List<InstanceDTO> list) {
        if (queryInfo.getInstanceId() != null) {
            return list.stream().filter(t -> t.getInstanceId() != null && t.getInstanceId().contains(queryInfo.getInstanceId()))
                    .collect(Collectors.toList());
        } else if (queryInfo.getStatus() != null) {
            return list.stream().filter(t -> t.getStatus() != null && t.getStatus().contains(queryInfo.getStatus()))
                    .collect(Collectors.toList());
        } else if (queryInfo.getVersion() != null) {
            return list.stream().filter(t -> t.getVersion() != null && t.getVersion().contains(queryInfo.getVersion()))
                    .collect(Collectors.toList());
        } else if (queryInfo.getParams() != null) {
            return list.stream().filter(t -> t.getInstanceId() != null && t.getInstanceId().contains(queryInfo.getParams()) ||
                    t.getStatus() != null && t.getStatus().contains(queryInfo.getParams()) ||
                    t.getVersion() != null && t.getVersion().contains(queryInfo.getParams())
            ).collect(Collectors.toList());
        }
        return list;
    }

    private List<InstanceDTO> getListPage(int page, int pageSize, List<InstanceDTO> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        int totalCount = list.size();
        int fromIndex = page * pageSize;
        if (fromIndex >= totalCount) {
            return Collections.emptyList();
        }
        int toIndex = ((page + 1) * pageSize);
        if (toIndex > totalCount) {
            toIndex = totalCount;
        }
        return list.subList(fromIndex, toIndex);
    }

}
