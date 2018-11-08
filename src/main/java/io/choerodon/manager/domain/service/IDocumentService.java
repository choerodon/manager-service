package io.choerodon.manager.domain.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.choerodon.eureka.event.EurekaEventPayload;

import java.io.IOException;

/**
 * swagger json的业务service
 *
 * @author xausky
 * @author wuguokai
 */
public interface IDocumentService {

    /**
     * 根据swagger json
     * 加上security等json node
     *
     * @param json swagger json
     * @return swagger json node
     * @throws IOException json解析异常
     */
    ObjectNode buildSwaggerJson(String json) throws IOException;

    /**
     * 根据服务id和版本获取swagger json
     * swagger表里有，则从swagger表里获取，没有则直接feign调用获取
     *
     * @param service 服务名，形如hap-user-service
     * @param version 版本，可为空，为空时从默认版本中获取swagger json
     * @return swagger json
     */
    String fetchSwaggerJsonByService(String service, String version);

    String getSwaggerJson(String name, String version, String json) throws IOException;

    String fetchSwaggerJsonByIp(EurekaEventPayload payload);

}
