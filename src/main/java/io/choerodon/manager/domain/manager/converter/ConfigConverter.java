package io.choerodon.manager.domain.manager.converter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.manager.api.dto.ConfigDTO;
import io.choerodon.manager.infra.dataobject.ConfigDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.domain.manager.entity.ConfigE;

/**
 * @author wuguokai
 */
@Component
public class ConfigConverter implements ConvertorI<ConfigE, ConfigDO, ConfigDTO> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String COMMON_EXCEPTION_1 = "error.config.parser";

    @Override
    public ConfigE dtoToEntity(ConfigDTO dto) {
        try {
            String value = null;
            if (dto.getValue() != null) {
                value = MAPPER.writeValueAsString(dto.getValue());
            }
            return new ConfigE(dto.getId(), dto.getName(), dto.getConfigVersion(),
                    dto.getIsDefault(), dto.getServiceId(), value, dto.getSource(), dto.getPublicTime(),
                    dto.getObjectVersionNumber());
        } catch (IOException e) {
            throw new CommonException(COMMON_EXCEPTION_1);
        }
    }

    @Override
    public ConfigDTO entityToDto(ConfigE entity) {
        try {
            Map<String, Object> value = new HashMap<>();
            if (entity.getValue() != null) {
                value = MAPPER.readValue(entity.getValue(), Map.class);
            }
            return new ConfigDTO(entity.getId(), entity.getName(),
                    entity.getConfigVersion(), entity.getDefault(),
                    entity.getServiceId(), value, entity.getSource(),
                    entity.getPublicTime(), entity.getObjectVersionNumber());
        } catch (IOException e) {
            throw new CommonException(COMMON_EXCEPTION_1);
        }
    }

    @Override
    public ConfigE doToEntity(ConfigDO dataObject) {
        return new ConfigE(dataObject.getId(), dataObject.getName(), dataObject.getConfigVersion(),
                dataObject.getIsDefault(), dataObject.getServiceId(), dataObject.getValue(),
                dataObject.getSource(), dataObject.getPublicTime(),
                dataObject.getObjectVersionNumber());

    }

    @Override
    public ConfigDO entityToDo(ConfigE entity) {
        ConfigDO configDO = new ConfigDO();
        BeanUtils.copyProperties(entity, configDO);
        return configDO;
    }

    @Override
    public ConfigDTO doToDto(ConfigDO dataObject) {
        try {
            Map<String, Object> value = new HashMap<>();
            if (dataObject.getValue() != null) {
                value = MAPPER.readValue(dataObject.getValue(), Map.class);
            }
            return new ConfigDTO(dataObject.getId(), dataObject.getName(), dataObject.getConfigVersion(),
                    dataObject.getIsDefault(), dataObject.getServiceId(), value, dataObject.getSource(),
                    dataObject.getPublicTime(), dataObject.getObjectVersionNumber());
        } catch (IOException e) {
            throw new CommonException(COMMON_EXCEPTION_1);
        }
    }

    @Override
    public ConfigDO dtoToDo(ConfigDTO dto) {
        try {
            String value = null;
            if (dto.getValue() != null) {
                value = MAPPER.writeValueAsString(dto.getValue());
            }
            return new ConfigDO(dto.getId(), dto.getName(), dto.getConfigVersion(),
                    dto.getIsDefault(), dto.getServiceId(), value, dto.getSource(), dto.getPublicTime(),
                    dto.getObjectVersionNumber());
        } catch (IOException e) {
            throw new CommonException(COMMON_EXCEPTION_1);
        }
    }
}
