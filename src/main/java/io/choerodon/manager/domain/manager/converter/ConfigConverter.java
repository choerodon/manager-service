package io.choerodon.manager.domain.manager.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.ConfigVO;
import io.choerodon.manager.domain.manager.entity.ConfigE;
import io.choerodon.manager.infra.dto.ConfigDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wuguokai
 */
@Component
public class ConfigConverter implements ConvertorI<ConfigE, ConfigDTO, ConfigVO> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String COMMON_EXCEPTION_1 = "error.config.parser";

    @Override
    public ConfigE dtoToEntity(ConfigVO dto) {
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
    @SuppressWarnings("unchecked")
    public ConfigVO entityToDto(ConfigE entity) {
        try {
            ConfigVO configVO = new ConfigVO();
            Map<String, Object> value = new HashMap<>();
            if (entity.getValue() != null) {
                value = MAPPER.readValue(entity.getValue(), Map.class);
            }
            BeanUtils.copyProperties(entity, configVO);
            configVO.setValue(value);
            return configVO;
        } catch (IOException e) {
            throw new CommonException(COMMON_EXCEPTION_1);
        }
    }

    @Override
    public ConfigE doToEntity(ConfigDTO dataObject) {
        ConfigE configE = new ConfigE();
        BeanUtils.copyProperties(dataObject, configE);
        return configE;

    }

    @Override
    public ConfigDTO entityToDo(ConfigE entity) {
        ConfigDTO configDTO = new ConfigDTO();
        BeanUtils.copyProperties(entity, configDTO);
        return configDTO;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ConfigVO doToDto(ConfigDTO dataObject) {
        try {
            ConfigVO configVO = new ConfigVO();
            Map<String, Object> value = new HashMap<>();
            if (dataObject.getValue() != null) {
                value = MAPPER.readValue(dataObject.getValue(), Map.class);
            }
            BeanUtils.copyProperties(dataObject, configVO);
            configVO.setValue(value);
            return configVO;
        } catch (IOException e) {
            throw new CommonException(COMMON_EXCEPTION_1);
        }
    }

    @Override
    public ConfigDTO dtoToDo(ConfigVO dto) {
        try {
            ConfigDTO configDTO = new ConfigDTO();
            String value = null;
            if (dto.getValue() != null) {
                value = MAPPER.writeValueAsString(dto.getValue());
            }
            BeanUtils.copyProperties(dto, configDTO);
            configDTO.setValue(value);
            return configDTO;
        } catch (IOException e) {
            throw new CommonException(COMMON_EXCEPTION_1);
        }
    }
}
