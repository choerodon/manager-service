package io.choerodon.manager.app.service;

import java.io.IOException;

/**
 * {@inheritDoc}
 *
 * @author superleader8@gmail.com
 * @data 2018/3/14
 */
public interface DocumentService {

    String getSwaggerJson(String name, String version) throws IOException;

    void manualRefresh(String serviceName, String version);
}
