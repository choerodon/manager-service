package io.choerodon.manager.api.eventhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.manager.api.dto.RegisterInstancePayload;
import io.choerodon.manager.domain.service.IDocumentService;
import io.choerodon.manager.domain.service.IRouteService;
import io.choerodon.manager.domain.service.SwaggerRefreshService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * eureka-instance消息队列的新消息监听处理
 *
 * @author zhipeng.zuo
 * @author wuguokai
 */
@Component
@RefreshScope
public class EurekaInstanceRegisteredListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EurekaInstanceRegisteredListener.class);

    private static final String STATUS_UP = "UP";

    private static final String REGISTER_TOPIC = "register-server";

    private final ObjectMapper mapper = new ObjectMapper();

    private Map<String, Integer> failTimeMap = new HashMap<>();

    @Value("${choerodon.swagger.skip.service}")
    private String[] skipServices;

    @Value("${choerodon.swagger.fetch.callBack:true}")
    private boolean fetchSwaggerJsonCallBack;

    @Value("${choerodon.swagger.fetch.time:10}")
    private Integer fetchSwaggerJsonTime;

    private IDocumentService iDocumentService;

    private SwaggerRefreshService swaggerRefreshService;

    private IRouteService iRouteService;

    private KafkaTemplate<byte[], byte[]> kafkaTemplate;


    public EurekaInstanceRegisteredListener(IDocumentService iDocumentService,
                                            SwaggerRefreshService swaggerRefreshService,
                                            IRouteService iRouteService,
                                            KafkaTemplate<byte[], byte[]> kafkaTemplate) {
        this.iDocumentService = iDocumentService;
        this.swaggerRefreshService = swaggerRefreshService;
        this.iRouteService = iRouteService;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 监听eureka-instance消息队列的新消息处理
     *
     * @param record 消息信息
     */
    @KafkaListener(topics = REGISTER_TOPIC)
    public void handle(ConsumerRecord<byte[], byte[]> record) {
        String message = new String(record.value());
        try {
            LOGGER.info("receive message from register-server, {}", message);
            RegisterInstancePayload payload = mapper.readValue(message, RegisterInstancePayload.class);
            if (!STATUS_UP.equals(payload.getStatus())) {
                LOGGER.info("skip message that status is not up, {}", payload);
                return;
            }
            boolean isSkipService =
                    Arrays.stream(skipServices).anyMatch(t -> t.equals(payload.getAppName()));
            if (isSkipService) {
                LOGGER.info("skip message that is skipServices, {}", payload);
                return;
            }
            Observable.just(payload)
                    .delay(2, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::msgConsumer);
        } catch (IOException e) {
            LOGGER.warn("error happened when handle message， {} cause {}", message, e.toString());
        }
    }

    private void msgConsumer(final RegisterInstancePayload instancePayload) {
        String json = iDocumentService.fetchSwaggerJsonByIp(instancePayload);
        if (StringUtils.isEmpty(json)) {
            LOGGER.info("fetched swagger json data is empty, {}", instancePayload);
            Integer time = failTimeMap.get(instancePayload.getInstanceAddress());
            if (time == null) {
                time = 0;
            }
            if (fetchSwaggerJsonCallBack) {
                instancePayload.setApiData(null);
                try {
                    if (time < fetchSwaggerJsonTime) {
                        kafkaTemplate.send(REGISTER_TOPIC, mapper.writeValueAsBytes(instancePayload));
                        failTimeMap.put(instancePayload.getInstanceAddress(), ++time);
                    } else {
                        failTimeMap.remove(instancePayload.getInstanceAddress());
                        LOGGER.warn("fetched swagger json data failed too many times {}", instancePayload);
                    }

                } catch (JsonProcessingException e) {
                    LOGGER.warn("error happened when instancePayload serialize {}", e.getMessage());
                }
            }
        } else {
            swaggerConsumer(instancePayload, json);
            permissionConsumer(instancePayload, json);
            routeConsumer(instancePayload, json);
            failTimeMap.remove(instancePayload.getInstanceAddress());
        }
    }
    private boolean swaggerConsumer(final RegisterInstancePayload payload, final String json) {
        try {
            swaggerRefreshService.updateOrInsertSwagger(payload, json);
            return true;
        } catch (Exception e) {
            LOGGER.warn("message has bean consumed failed when updateOrInsertSwagger, e {}", e.getMessage());
        }
        return false;
    }

    private boolean permissionConsumer(final RegisterInstancePayload payload, final String json) {
        try {
            swaggerRefreshService.parsePermission(payload, json);
            return true;
        } catch (Exception e) {
            LOGGER.warn("message has bean consumed failed when parsePermission, e {}", e.getMessage());
        }
        return false;
    }

    private boolean routeConsumer(final RegisterInstancePayload payload, final String json) {
        try {
            if (iRouteService.queryRouteByService(payload.getAppName()) == null) {
                iRouteService.autoRefreshRoute(json);
            }
            return true;
        } catch (Exception e) {
            LOGGER.warn("message has bean consumed failed when autoRefreshRoute, e {}", e.getMessage());
        }
        return false;
    }

}

