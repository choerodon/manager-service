package io.choerodon.manager.domain.manager.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.ConfigDTO;
import io.choerodon.manager.domain.manager.entity.ConfigE;
import io.choerodon.manager.infra.dataobject.ConfigDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
            ConfigE configE = new ConfigE();
            String value = null;
            if (dto.getValue() != null) {
                value = MAPPER.writeValueAsString(dto.getValue());
            }
           BeanUtils.copyProperties(dto, configE);
            configE.setValue(value);
            return configE;
        } catch (IOException e) {
            throw new CommonException(COMMON_EXCEPTION_1);
        }
    }

    @Override
    public ConfigDTO entityToDto(ConfigE entity) {
        try {
            ConfigDTO configDTO = new ConfigDTO();
            Map<String, Object> value = new HashMap<>();
            if (entity.getValue() != null) {
                value = MAPPER.readValue(entity.getValue(), Map.class);
            }
            BeanUtils.copyProperties(entity, configDTO);
            configDTO.setValue(value);
            return configDTO;
        } catch (IOException e) {
            throw new CommonException(COMMON_EXCEPTION_1);
        }
    }

    @Override
    public ConfigE doToEntity(ConfigDO dataObject) {
        ConfigE configE = new ConfigE();
        BeanUtils.copyProperties(dataObject, configE);
        return configE;

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
            ConfigDTO configDTO = new ConfigDTO();
            Map<String, Object> value = new HashMap<>();
            if (dataObject.getValue() != null) {
                value = MAPPER.readValue(dataObject.getValue(), Map.class);
            }
            BeanUtils.copyProperties(dataObject, configDTO);
            configDTO.setValue(value);
            return configDTO;
        } catch (IOException e) {
            throw new CommonException(COMMON_EXCEPTION_1);
        }
    }

    @Override
    public ConfigDO dtoToDo(ConfigDTO dto) {
        try {
            ConfigDO configDO = new ConfigDO();
            String value = null;
            if (dto.getValue() != null) {
                value = MAPPER.writeValueAsString(dto.getValue());
            }
            BeanUtils.copyProperties(dto, configDO);
            configDO.setValue(value);
            return configDO;
        } catch (IOException e) {
            throw new CommonException(COMMON_EXCEPTION_1);
        }
    }
}
