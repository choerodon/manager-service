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
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.Arrays;
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

    @Value("${choerodon.swagger.skip.service}")
    private String[] skipServices;

    @Value("${choerodon.swagger.fetch.time:10}")
    private Integer fetchSwaggerJsonTime;

    private IDocumentService iDocumentService;

    private SwaggerRefreshService swaggerRefreshService;

    private IRouteService iRouteService;

    public EurekaInstanceRegisteredListener(IDocumentService iDocumentService,
                                            SwaggerRefreshService swaggerRefreshService,
                                            IRouteService iRouteService) {
        this.iDocumentService = iDocumentService;
        this.swaggerRefreshService = swaggerRefreshService;
        this.iRouteService = iRouteService;
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
                    .map(this::msgConsumer)
                    .retryWhen(x -> x.zipWith(Observable.range(1, fetchSwaggerJsonTime),
                            (t, retryCount) -> {
                                if (retryCount >= fetchSwaggerJsonTime) {
                                    if (t instanceof RemoteAccessException) {
                                        LOGGER.warn("error.registerConsumer.fetchDataError, payload {} exception {}", payload, t);
                                    } else {
                                        LOGGER.warn("error.registerConsumer.msgConsumerError, payload {} exception {}", payload, t);
                                    }
                                }
                                return retryCount;
                            }).flatMap(y -> Observable.timer(2, TimeUnit.SECONDS)))
                    .subscribeOn(Schedulers.io())
                    .subscribe((RegisterInstancePayload registerInstancePayload) -> {
                    });
        } catch (IOException e) {
            LOGGER.warn("error happened when handle message， {} cause {}", message, e.getCause());
        }
    }

    private RegisterInstancePayload msgConsumer(final RegisterInstancePayload payload) {
        try {
            String json = iDocumentService.fetchSwaggerJsonByIp(payload);
            if (StringUtils.isEmpty(json)) {
                throw new RemoteAccessException("fetched swagger json data is empty, " + payload);
            }
            swaggerRefreshService.updateOrInsertSwagger(payload, json);
            swaggerRefreshService.parsePermission(payload, json);
            iRouteService.autoRefreshRoute(json);
        } catch (JsonProcessingException e) {
            LOGGER.warn("JsonProcessingException cause {}", e.getCause());
        }
        return payload;
    }


}

