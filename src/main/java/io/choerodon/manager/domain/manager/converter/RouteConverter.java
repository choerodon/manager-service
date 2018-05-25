package io.choerodon.manager.domain.manager.converter;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.manager.api.dto.RouteDTO;
import io.choerodon.manager.domain.factory.RouteEFactory;
import io.choerodon.manager.domain.manager.entity.RouteE;
import io.choerodon.manager.infra.dataobject.RouteDO;

/**
 * {@inheritDoc}
 *
 * @author superleader8@gmail.com
 * @author wuguokai
 */
@Component
public class RouteConverter implements ConvertorI<RouteE, RouteDO, RouteDTO> {
    @Override
    public RouteE doToEntity(RouteDO dataObject) {
        RouteE re = RouteEFactory.createRouteE();
        BeanUtils.copyProperties(dataObject, re);
        return re;
    }

    @Override
    public RouteDO entityToDo(RouteE entity) {
        RouteDO routeDO = new RouteDO();
        routeDO.setId(entity.getId());
        routeDO.setCustomSensitiveHeaders(entity.getCustomSensitiveHeaders());
        routeDO.setName(entity.getName());
        routeDO.setPath(entity.getPath());
        routeDO.setRetryable(entity.getRetryable());
        routeDO.setSensitiveHeaders(entity.getSensitiveHeaders());
        routeDO.setServiceId(entity.getServiceId());
        routeDO.setStripPrefix(entity.getStripPrefix());
        routeDO.setUrl(entity.getUrl());
        routeDO.setBuiltIn(entity.getBuiltIn());
        routeDO.setObjectVersionNumber(entity.getObjectVersionNumber());
        return routeDO;
    }

    @Override
    public RouteE dtoToEntity(RouteDTO routeDTO) {
        RouteE routeE = RouteEFactory.createRouteE();
        routeE.setId(routeDTO.getId());
        routeE.setCustomSensitiveHeaders(routeDTO.getCustomSensitiveHeaders());
        routeE.setName(routeDTO.getName());
        routeE.setPath(routeDTO.getPath());
        routeE.setRetryable(routeDTO.getRetryable());
        routeE.setSensitiveHeaders(routeDTO.getSensitiveHeaders());
        routeE.setServiceId(routeDTO.getServiceId());
        routeE.setStripPrefix(routeDTO.getStripPrefix());
        routeE.setUrl(routeDTO.getUrl());
        routeE.setObjectVersionNumber(routeDTO.getObjectVersionNumber());
        routeE.setBuiltIn(routeDTO.getBuiltIn());
        return routeE;
    }

    @Override
    public RouteDTO entityToDto(RouteE routeE) {
        RouteDTO routeDTO = new RouteDTO();
        routeDTO.setId(routeE.getId());
        routeDTO.setCustomSensitiveHeaders(routeE.getCustomSensitiveHeaders());
        routeDTO.setName(routeE.getName());
        routeDTO.setPath(routeE.getPath());
        routeDTO.setRetryable(routeE.getRetryable());
        routeDTO.setSensitiveHeaders(routeE.getSensitiveHeaders());
        routeDTO.setServiceId(routeE.getServiceId());
        routeDTO.setStripPrefix(routeE.getStripPrefix());
        routeDTO.setUrl(routeE.getUrl());
        routeDTO.setObjectVersionNumber(routeE.getObjectVersionNumber());
        routeDTO.setBuiltIn(routeE.getBuiltIn());
        return routeDTO;
    }
}
