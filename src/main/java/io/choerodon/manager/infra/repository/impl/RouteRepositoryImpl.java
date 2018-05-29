package io.choerodon.manager.infra.repository.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.domain.manager.entity.RouteE;
import io.choerodon.manager.domain.repository.RouteRepository;
import io.choerodon.manager.infra.dataobject.RouteDO;
import io.choerodon.manager.infra.mapper.RouteMapper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wuguokai
 */
@Component
public class RouteRepositoryImpl implements RouteRepository {
    private RouteMapper routeMapper;

    public RouteRepositoryImpl(RouteMapper routeMapper) {
        this.routeMapper = routeMapper;
    }

    @Override
    public RouteE queryRoute(RouteE routeE) {
        RouteDO routeDO = ConvertHelper.convert(routeE, RouteDO.class);
        return ConvertHelper.convert(routeMapper.selectOne(routeDO), RouteE.class);
    }

    @Override
    public RouteE addRoute(RouteE routeE) {
        RouteDO routeDO = ConvertHelper.convert(routeE, RouteDO.class);
        int isInsert = routeMapper.insert(routeDO);
        if (isInsert != 1) {
            throw new CommonException("error.insert.route");
        }
        return ConvertHelper.convert(routeMapper.selectByPrimaryKey(routeDO.getId()), RouteE.class);
    }

    @Override
    public RouteE updateRoute(RouteE routeE) {
        RouteDO oldRouteD = routeMapper.selectByPrimaryKey(routeE.getId());
        if (oldRouteD == null) {
            throw new CommonException("error.routeDO.not.exist");
        }
        if (oldRouteD.getBuiltIn()) {
            throw new CommonException("error.routeDO.updateBuiltIn");
        }
        RouteDO routeDO = ConvertHelper.convert(routeE, RouteDO.class);
        if (routeDO.getObjectVersionNumber() == null) {
            throw new CommonException("error.objectVersionNumber.empty");
        }
        routeDO.setBuiltIn(false);
        int isUpdate = routeMapper.updateByPrimaryKeySelective(routeDO);
        if (isUpdate != 1) {
            throw new CommonException("error.update.route");
        }
        return ConvertHelper.convert(routeMapper.selectByPrimaryKey(routeE.getId()), RouteE.class);
    }

    @Override
    public boolean deleteRoute(RouteE routeE) {
        RouteDO routeDO = ConvertHelper.convert(routeE, RouteDO.class);
        int isDelete = routeMapper.delete(routeDO);
        if (isDelete != 1) {
            throw new CommonException("error.delete.route");
        }
        return true;
    }

    @Override
    public List<RouteE> getAllRoute() {
        List<RouteDO> routeDOList = routeMapper.selectAll();
        return ConvertHelper.convertList(routeDOList, RouteE.class);
    }

    @Override
    public List<RouteE> addRoutesBatch(List<RouteE> routeEList) {
        return routeEList.stream().map(this::addRoute).collect(Collectors.toList());
    }

    @Override
    public Page<RouteE> pageAllRoutes(PageRequest pageRequest) {
        Page<RouteDO> routeDOPage = PageHelper.doPageAndSort(pageRequest, () -> routeMapper.selectAll());
        return ConvertPageHelper.convertPage(routeDOPage, RouteE.class);
    }
}
