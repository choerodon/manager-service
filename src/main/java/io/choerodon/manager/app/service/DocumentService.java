package io.choerodon.manager.app.service;

import java.io.IOException;

/**
 * {@inheritDoc}
 *
 * @author superleader8@gmail.com
 */
public interface DocumentService {

    String getSwaggerJson(String name, String version) throws IOException;

}
