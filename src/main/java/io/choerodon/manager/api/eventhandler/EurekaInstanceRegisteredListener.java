package io.choerodon.manager.api.eventhandler;

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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

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
    private static final long QUERY_INTERVAL = (3 * 1000);
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${choerodon.swagger.skip.service}")
    private String[] skipServices;

    @Value("${choerodon.register.executetTime:100}")
    private Integer executeTime;

    private ConcurrentMap<String, RegisterInstancePayload> map = new ConcurrentHashMap<>();

    private IDocumentService iDocumentService;

    private SwaggerRefreshService swaggerRefreshService;

    private IRouteService iRouteService;

    private AtomicInteger num = new AtomicInteger(0);

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
    @KafkaListener(topics = "register-server")
    public void handle(ConsumerRecord<byte[], byte[]> record) {
        String message = new String(record.value());
        try {
            LOGGER.info("receive message from register-server, {}", message);
            RegisterInstancePayload registerInstancePayload = mapper.readValue(message, RegisterInstancePayload.class);
            if (!STATUS_UP.equals(registerInstancePayload.getStatus())) {
                LOGGER.info("skip message that status is not up, {}", registerInstancePayload);
                return;
            }
            boolean isSkipService =
                    Arrays.stream(skipServices).anyMatch(t -> t.equals(registerInstancePayload.getAppName()));
            if (isSkipService) {
                LOGGER.info("skip message that is skipServices, {}", registerInstancePayload);
                return;
            }
            String key = registerInstancePayload.getAppName() + registerInstancePayload.getVersion();
            map.put(key, registerInstancePayload);
        } catch (IOException e) {
            LOGGER.warn("error happened when deserialize message， {}", message);
        }
    }


    @Scheduled(fixedDelay = QUERY_INTERVAL)
    protected void registerInstancePayloadConsume() {
        if (num.get() > 0) {
            return;
        }
        if (num.get() < 0) {
            num.set(0);
        }
        num.compareAndSet(0, map.size());
        map.entrySet().parallelStream().forEach(entry -> {
            try {
                handlerRegisterInstancePayload(entry.getValue(), entry.getKey());
            } catch (Exception e) {
                if (entry.getValue().getExecuteTime() % 10 == 0) {
                    LOGGER.warn("message has bean consumed failed when handlerRegisterInstancePayload {}", e.getMessage());
                }
            } finally {
                this.num.decrementAndGet();
            }
        });
    }

    private void handlerRegisterInstancePayload(final RegisterInstancePayload payload, final String key) {
        payload.increaseExecuteTime();
        if (payload.getExecuteTime() > this.executeTime) {
            LOGGER.info("message has bean consumed failed so many times, will discard this message,  {}", payload);
            map.remove(key);
        }
        String json = iDocumentService.fetchSwaggerJson(payload.getAppName(), payload.getVersion());
        if (StringUtils.isEmpty(json)) {
            if (payload.getExecuteTime() % 10 == 0) {
                LOGGER.info("fetched swagger json data is empty, {}", payload);
            }
            return;
        }
        boolean success = swaggerConsumer(payload, json) && permissionConsumer(payload, json) && routeConsumer(payload, json);
        if (success) {
            LOGGER.info("message has bean consumed successfully, remove from consumer queue {}", payload);
            map.remove(key);
        }
    }

    private boolean swaggerConsumer(final RegisterInstancePayload payload, final String json) {
        if (payload.getSwaggerStatus()) {
            return true;
        }
        try {
            swaggerRefreshService.updateOrInsertSwagger(payload, json);
            payload.setSwaggerStatusSuccess();
            return true;
        } catch (Exception e) {
            if (payload.getExecuteTime() % 10 == 0) {
                LOGGER.info("message has bean consumed failed when updateOrInsertSwagger, e {}", e.getMessage());
            }
        }
        return false;
    }

    private boolean permissionConsumer(final RegisterInstancePayload payload, final String json) {
        if (payload.getPermissionStatus()) {
            return true;
        }
        try {
            swaggerRefreshService.parsePermission(payload, json);
            payload.setPermissionStatusSuccess();
            return true;
        } catch (Exception e) {
            if (payload.getExecuteTime() % 10 == 0) {
                LOGGER.info("message has bean consumed failed when parsePermission, e {}", e.getMessage());
            }
        }
        return false;
    }

    private boolean routeConsumer(final RegisterInstancePayload payload, final String json) {
        if (payload.getRouteStatus()) {
            return true;
        }
        try {
            if (iRouteService.queryRouteByService(payload.getAppName()) == null) {
                iRouteService.autoRefreshRoute(json);
            }
            payload.setRouteStatusSuccess();
            return true;
        } catch (Exception e) {
            if (payload.getExecuteTime() % 10 == 0) {
                LOGGER.info("message has bean consumed failed when autoRefreshRoute, e {}", e.getMessage());
            }
        }
        return false;
    }

}

