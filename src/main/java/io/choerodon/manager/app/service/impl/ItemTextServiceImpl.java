package io.choerodon.manager.app.service.impl;

import io.choerodon.manager.api.dto.ConfigDTO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.app.service.ItemTextService;
import io.choerodon.manager.app.service.ConfigService;
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
    private ConfigService configService;

    public ItemTextServiceImpl(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public String getConfigText(Long configId, String type) {
        try {
            ConfigDTO configDTO = configService.query(configId);
            if (configDTO == null) {
                throw new CommonException("error.config.not.exist");
            }
            ConfigFileFormat configFileFormat = ConfigFileFormat.fromString(type);
            Builder builder = BuilderFactory.getBuilder(configFileFormat);
            return builder.build(configDTO.getValue());
        } catch (Exception e) {
            throw new CommonException("error.config.query");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @ConfigNotifyRefresh
    public void updateConfigText(Long configId, String type, String configText) {
        try {
            ConfigDTO configDTO = configService.query(configId);
            if (configDTO == null) {
                throw new CommonException("error.config.not.exist");
            }
            ConfigFileFormat configFileFormat = ConfigFileFormat.fromString(type);
            Parser parser = ParserFactory.getParser(configFileFormat);
            configDTO.setValue(parser.parse(configText));
            if (configService.update(configDTO.getId(), configDTO) == null) {
                throw new CommonException("error.config.update");
            }
        } catch (Exception e) {
            throw new CommonException("error.config.update");
        }
    }
}
