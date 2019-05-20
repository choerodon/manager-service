package io.choerodon.manager.api.eventhandler;

import io.choerodon.eureka.event.AbstractEurekaEventObserver;
import io.choerodon.eureka.event.EurekaEventPayload;
import io.choerodon.manager.domain.service.IActuatorRefreshService;
import io.choerodon.manager.domain.service.IDocumentService;
import io.choerodon.manager.domain.service.IRouteService;
import io.choerodon.manager.domain.service.ISwaggerRefreshService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class EurekaEventObserver extends AbstractEurekaEventObserver {
    private static final Logger LOGGER = LoggerFactory.getLogger(EurekaEventObserver.class);

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
        try {
            String json = iDocumentService.fetchSwaggerJsonByIp(payload);
            if (StringUtils.isEmpty(json)) {
                throw new RemoteAccessException("fetch swagger json data is empty, " + payload);
            }
            swaggerRefreshService.updateOrInsertSwagger(payload, json);
            iRouteService.autoRefreshRoute(json);
        } catch (Exception e) {
            LOGGER.info("process swagger data exception skip: {}, {}", payload, e.getMessage());
        }
        try {
            String actuatorJson = iDocumentService.fetchActuatorJson(payload);
            if (StringUtils.isEmpty(actuatorJson)) {
                throw new RemoteAccessException("fetch actuator json data is empty, " + payload);
            }
            actuatorRefreshService.updateOrInsertActuator(payload.getAppName(), payload.getVersion(), actuatorJson);
            actuatorRefreshService.sendActuatorEvent(actuatorJson, payload.getAppName());
        } catch (Exception e) {
            LOGGER.info("process actuator data exception skip: {}, {}", payload, e.getMessage());
        }

        try {
            String metadataJson = iDocumentService.fetchMetadataJson(payload);
            if (StringUtils.isEmpty(metadataJson)) {
                LOGGER.info("fetch metadata json data is empty skip: {}", payload);
            } else {
                actuatorRefreshService.sendMetadataEvent(metadataJson, payload.getAppName());
            }
        } catch (Exception e) {
            LOGGER.info("process metadata data exception skip: {}, {}", payload, e.getMessage());
        }
    }

    @Override
    public void receiveDownEvent(EurekaEventPayload payload) {
        // do nothing
    }
}
