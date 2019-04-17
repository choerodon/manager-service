package io.choerodon.manager.domain.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.eureka.event.EurekaEventPayload;
import io.choerodon.manager.domain.service.ISwaggerRefreshService;
import io.choerodon.manager.domain.service.VersionStrategy;
import io.choerodon.manager.infra.dataobject.SwaggerDO;
import io.choerodon.manager.infra.mapper.SwaggerMapper;
import org.springframework.stereotype.Service;

/**
 * 实现类
 *
 * @author zhipeng.zuo
 * @author wuguokai
 */
@Service
public class ISwaggerRefreshServiceImpl implements ISwaggerRefreshService {

    private SwaggerMapper swaggerMapper;

    private VersionStrategy versionStrategy;

    /**
     * 构造器
     */
    public ISwaggerRefreshServiceImpl(SwaggerMapper swaggerMapper,
                                      VersionStrategy versionStrategy) {
        this.swaggerMapper = swaggerMapper;
        this.versionStrategy = versionStrategy;
    }

    @Override
    public void updateOrInsertSwagger(EurekaEventPayload registerInstancePayload, String json) {
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

}
