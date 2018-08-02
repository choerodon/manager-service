package io.choerodon.manager.domain.service.impl;

import io.choerodon.manager.domain.factory.SwaggerEFactory;
import io.choerodon.manager.domain.manager.entity.RouteE;
import io.choerodon.manager.domain.service.IRouteService;
import io.choerodon.manager.domain.service.ISwaggerService;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.MultiKeyMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.UiConfiguration;

import java.util.*;


/**
 * 实现类
 *
 * @author zhipeng.zuo
 * @author wuguokai
 */
@Service
public class ISwaggerServiceImpl implements ISwaggerService {

    @Value("${choerodon.swagger.skip.service}")
    private String[] skipService;

    private IRouteService iRouteService;

    public ISwaggerServiceImpl(IRouteService iRouteService) {
        this.iRouteService = iRouteService;
    }

    /**
     * 单元测试支持
     * @param iRouteService
     */
    public void setIRouteService(IRouteService iRouteService) {
        this.iRouteService = iRouteService;
    }

    @Override
    public List<SwaggerResource> getSwaggerResource() {
        List<SwaggerResource> resources = new LinkedList<>();
        MultiKeyMap multiKeyMap = iRouteService.getAllRunningInstances();
        Set set = multiKeyMap.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            MultiKey multiKey = (MultiKey) iterator.next();
            RouteE localRouteE = (RouteE) multiKeyMap.get(multiKey);
            if (localRouteE.getServiceId() != null) {
                boolean isSkipService = Arrays.stream(skipService).anyMatch(t -> t.equals(localRouteE.getServiceId()));
                if (!isSkipService) {
                    SwaggerResource resource = new SwaggerResource();
                    resource.setName(localRouteE.getName() + ":" + localRouteE.getServiceId());
                    resource.setSwaggerVersion("2.0");
                    resource.setLocation("/docs/" + localRouteE.getName() + "?version=" + multiKey.getKey(1));
                    resources.add(resource);
                }
            }
        }
        return resources;
    }

    @Override
    public UiConfiguration getUiConfiguration() {
        return SwaggerEFactory.createSwaggerE().getUiConfiguration();
    }

    @Override
    public SecurityConfiguration getSecurityConfiguration() {
        return SwaggerEFactory.createSwaggerE().getSecurityConfiguration();
    }
}
