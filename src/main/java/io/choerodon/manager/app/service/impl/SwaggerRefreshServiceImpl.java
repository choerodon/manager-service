package io.choerodon.manager.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.eureka.event.EurekaEventPayload;
import io.choerodon.manager.app.service.SwaggerRefreshService;
import io.choerodon.manager.app.service.VersionStrategy;
import io.choerodon.manager.infra.dto.SwaggerDTO;
import io.choerodon.manager.infra.mapper.SwaggerMapper;
import org.springframework.stereotype.Service;

/**
 * 实现类
 *
 * @author zhipeng.zuo
 * @author wuguokai
 */
@Service
public class SwaggerRefreshServiceImpl implements SwaggerRefreshService {

    private SwaggerMapper swaggerMapper;

    private VersionStrategy versionStrategy;

    /**
     * 构造器
     */
    public SwaggerRefreshServiceImpl(SwaggerMapper swaggerMapper,
                                     VersionStrategy versionStrategy) {
        this.swaggerMapper = swaggerMapper;
        this.versionStrategy = versionStrategy;
    }

    @Override
    public void updateOrInsertSwagger(EurekaEventPayload registerInstancePayload, String json) {
        SwaggerDTO example = new SwaggerDTO();
        example.setServiceVersion(registerInstancePayload.getVersion());
        example.setServiceName(registerInstancePayload.getAppName());
        SwaggerDTO swagger = swaggerMapper.selectOne(example);
        if (swagger != null) {
            swagger.setValue(json);
            if (swaggerMapper.updateByPrimaryKey(swagger) != 1) {
                throw new CommonException("error.swagger.update");
            }
        } else {
            SwaggerDTO swaggerDTO = new SwaggerDTO();
            swaggerDTO.setServiceName(registerInstancePayload.getAppName());
            swaggerDTO.setServiceVersion(registerInstancePayload.getVersion());
            swaggerDTO.setValue(json);
            SwaggerDTO queryDefault = new SwaggerDTO();
            queryDefault.setServiceName(registerInstancePayload.getAppName());
            queryDefault.setDefault(true);
            SwaggerDTO defaultVersion = swaggerMapper.selectOne(queryDefault);
            if (defaultVersion == null) {
                swaggerDTO.setDefault(true);
            } else if (versionStrategy
                    .compareVersion(registerInstancePayload.getVersion(), defaultVersion.getServiceVersion()) > 0) {
                swaggerDTO.setDefault(true);
                defaultVersion.setDefault(false);
                if (swaggerMapper.updateByPrimaryKeySelective(defaultVersion) != 1) {
                    throw new CommonException("error.swagger.update");
                }
            } else {
                swaggerDTO.setDefault(false);
            }
            if (swaggerMapper.insert(swaggerDTO) != 1) {
                throw new CommonException("error.swagger.insert");
            }
        }
    }

}
