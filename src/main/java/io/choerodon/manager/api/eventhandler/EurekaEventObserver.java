package io.choerodon.manager.api.eventhandler;

import io.choerodon.eureka.event.AbstractEurekaEventObserver;
import io.choerodon.eureka.event.EurekaEventPayload;
import io.choerodon.manager.domain.service.IActuatorRefreshService;
import io.choerodon.manager.domain.service.IDocumentService;
import io.choerodon.manager.domain.service.IRouteService;
import io.choerodon.manager.domain.service.ISwaggerRefreshService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class EurekaEventObserver extends AbstractEurekaEventObserver {

    private IDocumentService iDocumentService;

    private ISwaggerRefreshService swaggerRefreshService;

    private IRouteService iRouteService;

    @Autowired
    private IActuatorRefreshService actuatorRefreshService;

    public EurekaEventObserver(IDocumentService iDocumentService,
                               ISwaggerRefreshService swaggerRefreshService,
                               IRouteService iRouteService) {
        this.iDocumentService = iDocumentService;
        this.swaggerRefreshService = swaggerRefreshService;
        this.iRouteService = iRouteService;
    }

    @Override
    public void receiveUpEvent(EurekaEventPayload payload) {
        String json = iDocumentService.fetchSwaggerJsonByIp(payload);
        if (StringUtils.isEmpty(json)) {
            throw new RemoteAccessException("fetch swagger json data is empty, " + payload);
        }
        swaggerRefreshService.updateOrInsertSwagger(payload, json);
        iRouteService.autoRefreshRoute(json);
        String actuatorJson = iDocumentService.fetchActuatorJson(payload);
        if (StringUtils.isEmpty(json)) {
            throw new RemoteAccessException("fetch actuator json data is empty, " + payload);
        }
        actuatorRefreshService.updateOrInsertActuator(payload.getAppName(), payload.getVersion(), actuatorJson);
        actuatorRefreshService.sendEvent(actuatorJson, payload.getAppName());
    }

    @Override
    public void receiveDownEvent(EurekaEventPayload payload) {
       // do nothing
    }
}
