package io.choerodon.manager.domain.manager.converter;

import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.manager.domain.manager.entity.SwaggerE;
import io.choerodon.manager.infra.dataobject.SwaggerDO;

/**
 * @author superleader8@gmail.com
 * @data 2018/3/11
 */
@Component
public class SwaggerConverter implements ConvertorI<SwaggerE, SwaggerDO, Object> {

    @Override
    public SwaggerE doToEntity(SwaggerDO dataObject) {
        SwaggerE se = new SwaggerE();
        se.setDefault(dataObject.getDefault());
        se.setId(dataObject.getId());
        se.setServiceName(dataObject.getServiceName());
        se.setServiceVersion(dataObject.getServiceVersion());
        se.setValue(dataObject.getValue());
        return se;
    }

    @Override
    public SwaggerDO entityToDo(SwaggerE entity) {
        SwaggerDO swaggerDO = new SwaggerDO();
        swaggerDO.setDefault(entity.getDefault());
        swaggerDO.setId(entity.getId());
        swaggerDO.setServiceName(entity.getServiceName());
        swaggerDO.setServiceVersion(entity.getServiceVersion());
        swaggerDO.setValue(entity.getValue());
        return swaggerDO;
    }
}
