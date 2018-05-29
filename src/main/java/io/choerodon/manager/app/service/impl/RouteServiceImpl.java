package io.choerodon.manager.app.service.impl;

import java.util.List;

import io.choerodon.manager.infra.dataobject.RouteDO;
import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.manager.api.dto.RouteDTO;
import io.choerodon.manager.app.service.RouteService;
import io.choerodon.manager.domain.factory.RouteEFactory;
import io.choerodon.manager.domain.manager.entity.RouteE;
import io.choerodon.manager.domain.repository.RouteRepository;
import io.choerodon.manager.domain.service.IRouteService;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 应用层实现
 *
 * @author wuguokai
 */
@Component
public class RouteServiceImpl implements RouteService {
    private IRouteService irouteService;
    private RouteRepository routeRepository;


    /**
     * 构造器
     */
    public RouteServiceImpl(IRouteService irouteService, RouteRepository routeRepository) {
        this.irouteService = irouteService;
        this.routeRepository = routeRepository;
    }

    @Override
    public Page<RouteDTO> list(PageRequest pageRequest, RouteDO routeDO, String params) {
        Page<RouteE> routeEPage = irouteService.pageAll(pageRequest, routeDO, params);
        return ConvertPageHelper.convertPage(routeEPage, RouteDTO.class);
    }

    @Override
    public RouteDTO create(RouteDTO routeDTO) {
        if (routeDTO.getBuiltIn() == null) {
            routeDTO.setBuiltIn(false);
        }
        return ConvertHelper.convert(ConvertHelper.convert(routeDTO, RouteE.class).addRoute(), RouteDTO.class);
    }

    @Override
    public RouteDTO update(Long id, RouteDTO routeDTO) {
        routeDTO.setId(id);
        return ConvertHelper.convert(ConvertHelper.convert(routeDTO, RouteE.class).updateRoute(), RouteDTO.class);
    }

    @Override
    public Boolean delete(Long routeId) {
        RouteE routeE = RouteEFactory.createRouteE();
        routeE.setId(routeId);
        return routeE.deleteRoute();
    }

    @Override
    public List<RouteDTO> addRoutesBatch(List<RouteDTO> routeDTOList) {
        List<RouteE> routeEList = irouteService.addRoutes(ConvertHelper.convertList(routeDTOList, RouteE.class));
        return ConvertHelper.convertList(routeEList, RouteDTO.class);
    }

    @Override
    public List<RouteDTO> getAllRoute() {
        return ConvertHelper.convertList(irouteService.getAll(), RouteDTO.class);
    }

    @Override
    public RouteDTO queryByName(String name) {
        RouteDTO routeDTO = new RouteDTO();
        routeDTO.setName(name);
        return ConvertHelper.convert(
                routeRepository.queryRoute(
                        ConvertHelper.convert(routeDTO, RouteE.class)),
                RouteDTO.class);
    }
}
