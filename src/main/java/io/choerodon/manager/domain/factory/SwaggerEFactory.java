package io.choerodon.manager.domain.factory;

import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.manager.domain.manager.entity.SwaggerE;

/**
 * @author superleader8@gmail.com
 * @data 2018/3/16
 */
@Component
public class SwaggerEFactory {

    private SwaggerEFactory() {
    }

    public static SwaggerE createSwaggerE() {
        return ApplicationContextHelper.getSpringFactory().getBean(SwaggerE.class);
    }

}
