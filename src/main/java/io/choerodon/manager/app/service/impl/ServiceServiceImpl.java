package io.choerodon.manager.app.service.impl;

import static io.choerodon.manager.infra.common.utils.VersionUtil.METADATA_VERSION;

import java.util.ArrayList;
import java.util.List;

import com.netflix.appinfo.InstanceInfo;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.manager.api.dto.InstanceDTO;
import io.choerodon.manager.api.dto.ServiceDTO;
import io.choerodon.manager.app.service.ServiceService;
import io.choerodon.manager.domain.manager.entity.ServiceE;
import io.choerodon.manager.domain.service.IServiceService;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author wuguokai
 */
@Component
public class ServiceServiceImpl implements ServiceService {

    private IServiceService iserviceService;

    private DiscoveryClient discoveryClient;

    public ServiceServiceImpl(IServiceService iserviceService, DiscoveryClient discoveryClient) {
        this.iserviceService = iserviceService;
        this.discoveryClient = discoveryClient;
    }

    @Override
    public Page<ServiceDTO> pageAll(PageRequest pageRequest) {
        Page<ServiceE> serviceEPage = iserviceService.pageAll(pageRequest);
        return ConvertPageHelper.convertPage(serviceEPage, ServiceDTO.class);
    }

    @Override
    public List<InstanceDTO> getInstancesByService(String service) {
        List<InstanceDTO> instanceInfoList = new ArrayList<>();
        for (ServiceInstance serviceInstance : discoveryClient.getInstances(service)) {
            if (serviceInstance instanceof EurekaDiscoveryClient.EurekaServiceInstance) {
                EurekaDiscoveryClient.EurekaServiceInstance eurekaServiceInstance =
                        (EurekaDiscoveryClient.EurekaServiceInstance) serviceInstance;
                InstanceInfo info = eurekaServiceInstance.getInstanceInfo();
                instanceInfoList.add(new InstanceDTO(info.getInstanceId(), info.getAppName(),
                        info.getMetadata().get(METADATA_VERSION), info.getStatus().name()));
            }
        }
        return instanceInfoList;
    }
}
