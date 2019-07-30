package io.choerodon.manager.infra.asserts;

import io.choerodon.core.exception.ext.NotExistedException;
import io.choerodon.manager.infra.dto.ConfigDTO;
import io.choerodon.manager.infra.mapper.ConfigMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author superlee
 * @since 2019-07-30
 */
@Component
public class ConfigAssertHelper extends AssertHelper {

    private ConfigMapper configMapper;

    public ConfigAssertHelper(ConfigMapper configMapper) {
        this.configMapper = configMapper;
    }

    public ConfigDTO notExisted(Long id) {
        return notExisted(id, "error.config.not.exist");
    }

    public ConfigDTO notExisted(Long id, String message) {
        return Optional.ofNullable(configMapper.selectByPrimaryKey(id)).orElseThrow(() -> new NotExistedException(message));
    }


}
