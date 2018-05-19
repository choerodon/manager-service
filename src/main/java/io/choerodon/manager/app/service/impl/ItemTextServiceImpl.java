package io.choerodon.manager.app.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.ServiceConfigDTO;
import io.choerodon.manager.app.service.ItemTextService;
import io.choerodon.manager.app.service.ServiceConfigService;
import io.choerodon.manager.infra.common.annotation.ConfigNotifyRefresh;
import io.choerodon.manager.infra.common.utils.ConfigFileFormat;
import io.choerodon.manager.infra.common.utils.format.builder.Builder;
import io.choerodon.manager.infra.common.utils.format.builder.BuilderFactory;
import io.choerodon.manager.infra.common.utils.format.parser.Parser;
import io.choerodon.manager.infra.common.utils.format.parser.ParserFactory;

/**
 * 实现类
 *
 * @author wuguokai
 */
@Component
public class ItemTextServiceImpl implements ItemTextService {
    private ServiceConfigService serviceConfigService;

    public ItemTextServiceImpl(ServiceConfigService serviceConfigService) {
        this.serviceConfigService = serviceConfigService;
    }

    @Override
    public String getConfigText(Long configId, String type) {
        try {
            ServiceConfigDTO serviceConfigDTO = serviceConfigService.query(configId);
            if (serviceConfigDTO == null) {
                throw new CommonException("error.config.not.exist");
            }
            ConfigFileFormat configFileFormat = ConfigFileFormat.fromString(type);
            Builder builder = BuilderFactory.getBuilder(configFileFormat);
            return builder.build(serviceConfigDTO.getValue());
        } catch (Exception e) {
            throw new CommonException("error.config.query");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @ConfigNotifyRefresh
    public void updateConfigText(Long configId, String type, String configText) {
        try {
            ServiceConfigDTO serviceConfigDTO = serviceConfigService.query(configId);
            if (serviceConfigDTO == null) {
                throw new CommonException("error.config.not.exist");
            }
            ConfigFileFormat configFileFormat = ConfigFileFormat.fromString(type);
            Parser parser = ParserFactory.getParser(configFileFormat);
            serviceConfigDTO.setValue(parser.parse(configText));
            if (serviceConfigService.update(serviceConfigDTO.getId(), serviceConfigDTO) == null) {
                throw new CommonException("error.config.update");
            }
        } catch (Exception e) {
            throw new CommonException("error.config.update");
        }
    }
}
