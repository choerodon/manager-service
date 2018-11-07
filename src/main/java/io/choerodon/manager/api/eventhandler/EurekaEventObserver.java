package io.choerodon.manager.api.eventhandler;

import io.choerodon.eureka.event.AbstractEurekaEventObserver;
import io.choerodon.eureka.event.EurekaEventPayload;
import io.choerodon.manager.domain.service.IDocumentService;
import io.choerodon.manager.domain.service.IRouteService;
import io.choerodon.manager.domain.service.SwaggerRefreshService;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class EurekaEventObserver extends AbstractEurekaEventObserver {

    private IDocumentService iDocumentService;

    private SwaggerRefreshService swaggerRefreshService;

    private IRouteService iRouteService;

    public EurekaEventObserver(IDocumentService iDocumentService,
                               SwaggerRefreshService swaggerRefreshService,
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
    }

    @Override
    public void receiveDownEvent(EurekaEventPayload payload) {
       // do nothing
    }
}
