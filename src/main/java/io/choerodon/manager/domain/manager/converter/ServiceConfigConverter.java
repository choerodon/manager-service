package io.choerodon.manager.domain.manager.converter;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.ServiceConfigDTO;
import io.choerodon.manager.domain.manager.entity.ServiceConfigE;
import io.choerodon.manager.infra.dataobject.ServiceConfigDO;

/**
 * @author wuguokai
 */
@Component
public class ServiceConfigConverter implements ConvertorI<ServiceConfigE, ServiceConfigDO, ServiceConfigDTO> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String COMMON_EXCEPTION_1 = "error.config.parser";

    @Override
    public ServiceConfigE dtoToEntity(ServiceConfigDTO dto) {
        try {
            String value = MAPPER.writeValueAsString(dto.getValue());
            return new ServiceConfigE(dto.getId(), dto.getName(), dto.getConfigVersion(),
                    dto.getDefault(), dto.getServiceId(), value, dto.getSource(), dto.getPublicTime(),
                    dto.getObjectVersionNumber());
        } catch (IOException e) {
            throw new CommonException(COMMON_EXCEPTION_1);
        }
    }

    @Override
    public ServiceConfigDTO entityToDto(ServiceConfigE entity) {
        try {
            Map<String, Object> value = MAPPER.readValue(entity.getValue(), Map.class);
            return new ServiceConfigDTO(entity.getId(), entity.getName(),
                    entity.getConfigVersion(), entity.getDefault(),
                    entity.getServiceId(), value, entity.getSource(),
                    entity.getPublicTime(), entity.getObjectVersionNumber());
        } catch (IOException e) {
            throw new CommonException(COMMON_EXCEPTION_1);
        }
    }

    @Override
    public ServiceConfigE doToEntity(ServiceConfigDO dataObject) {
        return new ServiceConfigE(dataObject.getId(), dataObject.getName(), dataObject.getConfigVersion(),
                dataObject.getDefault(), dataObject.getServiceId(), dataObject.getValue(),
                dataObject.getSource(), dataObject.getPublicTime(),
                dataObject.getObjectVersionNumber());

    }

    @Override
    public ServiceConfigDO entityToDo(ServiceConfigE entity) {
        ServiceConfigDO serviceConfigDO = new ServiceConfigDO();
        BeanUtils.copyProperties(entity, serviceConfigDO);
        return serviceConfigDO;
    }

    @Override
    public ServiceConfigDTO doToDto(ServiceConfigDO dataObject) {
        try {
            Map<String, Object> value = MAPPER.readValue(dataObject.getValue(), Map.class);
            return new ServiceConfigDTO(dataObject.getId(), dataObject.getName(), dataObject.getConfigVersion(),
                    dataObject.getDefault(), dataObject.getServiceId(), value, dataObject.getSource(),
                    dataObject.getPublicTime(), dataObject.getObjectVersionNumber());
        } catch (IOException e) {
            throw new CommonException(COMMON_EXCEPTION_1);
        }
    }

    @Override
    public ServiceConfigDO dtoToDo(ServiceConfigDTO dto) {
        try {
            String value = MAPPER.writeValueAsString(dto.getValue());
            return new ServiceConfigDO(dto.getId(), dto.getName(), dto.getConfigVersion(),
                    dto.getDefault(), dto.getServiceId(), value, dto.getSource(), dto.getPublicTime(),
                    dto.getObjectVersionNumber());
        } catch (IOException e) {
            throw new CommonException(COMMON_EXCEPTION_1);
        }
    }
}
