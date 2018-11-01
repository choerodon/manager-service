package io.choerodon.manager.domain.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.RegisterInstancePayload;
import io.choerodon.manager.domain.service.SwaggerRefreshService;
import io.choerodon.manager.domain.service.VersionStrategy;
import io.choerodon.manager.infra.dataobject.SwaggerDO;
import io.choerodon.manager.infra.mapper.SwaggerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * 实现类
 *
 * @author zhipeng.zuo
 * @author wuguokai
 */
@Service
public class ISwaggerRefreshServiceImpl implements SwaggerRefreshService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ISwaggerRefreshServiceImpl.class);
    private static final String SWAGGER_TOPIC_NAME = "manager-service";
    private final ObjectMapper mapper = new ObjectMapper();
    private SwaggerMapper swaggerMapper;

    private VersionStrategy versionStrategy;

    private KafkaTemplate<byte[], byte[]> kafkaTemplate;

    /**
     * 构造器
     */
    public ISwaggerRefreshServiceImpl(SwaggerMapper swaggerMapper,
                                      VersionStrategy versionStrategy,
                                      KafkaTemplate kafkaTemplate) {
        this.swaggerMapper = swaggerMapper;
        this.versionStrategy = versionStrategy;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void updateOrInsertSwagger(RegisterInstancePayload registerInstancePayload, String json) {
        SwaggerDO example = new SwaggerDO();
        example.setServiceVersion(registerInstancePayload.getVersion());
        example.setServiceName(registerInstancePayload.getAppName());
        SwaggerDO swagger = swaggerMapper.selectOne(example);
        if (swagger != null) {
            swagger.setValue(json);
            if (swaggerMapper.updateByPrimaryKey(swagger) != 1) {
                throw new CommonException("error.swagger.update");
            }
        } else {
            SwaggerDO swaggerDO = new SwaggerDO();
            swaggerDO.setServiceName(registerInstancePayload.getAppName());
            swaggerDO.setServiceVersion(registerInstancePayload.getVersion());
            swaggerDO.setValue(json);
            SwaggerDO queryDefault = new SwaggerDO();
            queryDefault.setServiceName(registerInstancePayload.getAppName());
            queryDefault.setDefault(true);
            SwaggerDO defaultVersion = swaggerMapper.selectOne(queryDefault);
            if (defaultVersion == null) {
                swaggerDO.setDefault(true);
            } else if (versionStrategy
                    .compareVersion(registerInstancePayload.getVersion(), defaultVersion.getServiceVersion()) > 0) {
                swaggerDO.setDefault(true);
                defaultVersion.setDefault(false);
                if (swaggerMapper.updateByPrimaryKeySelective(defaultVersion) != 1) {
                    throw new CommonException("error.swagger.update");
                }
            } else {
                swaggerDO.setDefault(false);
            }
            if (swaggerMapper.insert(swaggerDO) != 1) {
                throw new CommonException("error.swagger.insert");
            }
        }

    }

    @Override
    public void parsePermission(RegisterInstancePayload registerInstancePayload, String json) throws JsonProcessingException {
        registerInstancePayload.setApiData(json);
        String data = mapper.writeValueAsString(registerInstancePayload);
        ListenableFuture<SendResult<byte[], byte[]>> resultListenableFuture = kafkaTemplate.send(SWAGGER_TOPIC_NAME, data.getBytes());
        resultListenableFuture.addCallback((SendResult result) ->
            LOGGER.info("parsePermission send message to kafka success, {}", registerInstancePayload)
        , (Throwable ex) -> {
            LOGGER.info("parsePermission send message to kafka failed, {} {}", registerInstancePayload, ex.getCause());
            throw new CommonException("error send message to kafka");
        });
    }
}
