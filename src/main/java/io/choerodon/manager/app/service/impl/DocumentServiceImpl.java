package io.choerodon.manager.app.service.impl;

import io.choerodon.manager.app.service.DocumentService;
import io.choerodon.manager.domain.service.IDocumentService;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * 实现类
 *
 * @author superleader8@gmail.com
 * @data 2018/3/14
 */
@Component
public class DocumentServiceImpl implements DocumentService {

    private IDocumentService service;

    public DocumentServiceImpl(IDocumentService service) {
        this.service = service;
    }

    @Override
    public String getSwaggerJson(String name, String version) throws IOException {
        return service.getSwaggerJson(name, version);
    }

}
