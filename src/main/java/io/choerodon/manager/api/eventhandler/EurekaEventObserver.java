package io.choerodon.manager.api.eventhandler;

import io.choerodon.eureka.event.AbstractEurekaEventObserver;
import io.choerodon.eureka.event.EurekaEventPayload;
import io.choerodon.manager.app.service.ActuatorRefreshService;
import io.choerodon.manager.app.service.DocumentService;
import io.choerodon.manager.app.service.RouteService;
import io.choerodon.manager.app.service.SwaggerRefreshService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class EurekaEventObserver extends AbstractEurekaEventObserver {
    private static final Logger LOGGER = LoggerFactory.getLogger(EurekaEventObserver.class);

    private DocumentService documentService;

    private SwaggerRefreshService swaggerRefreshService;


    private RouteService routeService;

    private ActuatorRefreshService actuatorRefreshService;

    public EurekaEventObserver(DocumentService documentService,
                               SwaggerRefreshService swaggerRefreshService,
                               ActuatorRefreshService actuatorRefreshService,
                               RouteService routeService) {
        this.documentService = documentService;
        this.swaggerRefreshService = swaggerRefreshService;
        this.actuatorRefreshService = actuatorRefreshService;
        this.routeService = routeService;
    }

    @Override
    public void receiveUpEvent(EurekaEventPayload payload) {
        try {
            String json = documentService.fetchSwaggerJsonByIp(payload);
            if (StringUtils.isEmpty(json)) {
                throw new RemoteAccessException("fetch swagger json data is empty, " + payload);
            }
            swaggerRefreshService.updateOrInsertSwagger(payload, json);
            routeService.autoRefreshRoute(json);
        } catch (Exception e) {
            LOGGER.warn("process swagger data exception skip: {}", payload, e);
        }
        try {
            String actuatorJson = documentService.fetchActuatorJson(payload);
            if (StringUtils.isEmpty(actuatorJson)) {
                throw new RemoteAccessException("fetch actuator json data is empty, " + payload);
            }
            if(actuatorRefreshService.updateOrInsertActuator(payload.getAppName(), payload.getVersion(), actuatorJson)){
                LOGGER.info("actuator data saga apply success: {}", payload.getId());
            } else {
                LOGGER.info("actuator data not change skip: {}", payload.getId());
            }
        } catch (Exception e) {
            LOGGER.warn("process actuator data exception skip: {}", payload, e);
        }

        try {
            String metadataJson = documentService.fetchMetadataJson(payload);
            if (StringUtils.isEmpty(metadataJson)) {
                LOGGER.info("fetch metadata json data is empty skip: {}", payload.getId());
            } else {
                actuatorRefreshService.sendMetadataEvent(metadataJson, payload.getAppName());
            }
        } catch (Exception e) {
            LOGGER.warn("process metadata data exception skip: {}", payload, e);
        }
    }

    @Override
    public void receiveDownEvent(EurekaEventPayload payload) {
        // do nothing
    }
}
