package io.choerodon.manager.api.validator;

import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.infra.common.utils.ConfigFileFormat;

/**
 * @author wuguokai
 */
public class ConfigFileTypeValidator {
    public static void validate(String type) {
        boolean rightType = false;
        for (ConfigFileFormat mt : ConfigFileFormat.values()) {
            if (mt.getValue().equals(type)) {
                rightType = true;
            }
        }
        if (!rightType) {
            throw new CommonException("error.file.type.illegal");
        }
    }
}
