package io.choerodon.manager.domain.service;

import java.io.IOException;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * swagger json的业务service
 *
 * @author xausky
 * @author wuguokai
 */
public interface IDocumentService {

    /**
     * 根据服务id和版本获取swagger json node
     * 加上security等json node
     *
     * @param service 服务名，形如hap-user-service
     * @param version 版本，可为空，为空时从默认版本中获取swagger json
     * @return swagger json node
     * @throws IOException json解析异常
     */
    ObjectNode getSwaggerJsonByIdAndVersion(String service, String version) throws IOException;

    /**
     * 根据服务id和版本获取swagger json
     * swagger表里有，则从swagger表里获取，没有则直接feign调用获取
     *
     * @param service 服务名，形如hap-user-service
     * @param version 版本，可为空，为空时从默认版本中获取swagger json
     * @return swagger json
     */
    String fetchSwaggerJsonByService(String service, String version);

    String getSwaggerJson(String name, String version) throws IOException;

    void manualRefresh(String serviceName, String version);

    String fetchSwaggerJson(String service, String version);

    interface RefreshSwaggerListener {

        /**
         * 刷新swagger json的回调
         *
         * @param service 服务名
         * @param json    该服务的swagger json
         */
        void refresh(String service, String json);

    }
}
