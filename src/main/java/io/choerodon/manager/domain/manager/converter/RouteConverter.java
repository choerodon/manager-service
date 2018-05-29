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
        BeanUtils.copyProperties(entity,  routeDO);
        return routeDO;
    }

    @Override
    public RouteE dtoToEntity(RouteDTO routeDTO) {
        RouteE routeE = RouteEFactory.createRouteE();
        BeanUtils.copyProperties(routeDTO,  routeE);
        return routeE;
    }

    @Override
    public RouteDTO entityToDto(RouteE routeE) {
        RouteDTO routeDTO = new RouteDTO();
        BeanUtils.copyProperties(routeE,  routeDTO);
        return routeDTO;
    }
}
