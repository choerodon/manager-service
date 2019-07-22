package io.choerodon.manager.infra.repository.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.domain.manager.entity.RouteE;
import io.choerodon.manager.domain.repository.RouteRepository;
import io.choerodon.manager.infra.common.annotation.RouteNotifyRefresh;
import io.choerodon.manager.infra.dto.RouteDTO;
import io.choerodon.manager.infra.mapper.RouteMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wuguokai
 */
@Component
public class RouteRepositoryImpl implements RouteRepository {

    private RouteMapper routeMapper;

    @Value("${eureka.client.serviceUrl.defaultZone}")
    private String registerUrl;

    private RestTemplate restTemplate = new RestTemplate();

    private static final String ADD_ZUUL_ROOT_URL = "/zuul";
    private static final String DELETE_ZUUL_ROOT_URL = "/zuul/delete";

    public RouteRepositoryImpl(RouteMapper routeMapper) {
        this.routeMapper = routeMapper;
    }

    @Override
    public RouteE queryRoute(RouteE routeE) {
        RouteDTO routeDTO = ConvertHelper.convert(routeE, RouteDTO.class);
        return ConvertHelper.convert(routeMapper.selectOne(routeDTO), RouteE.class);
    }

    @Override
    @RouteNotifyRefresh
    @Transactional(rollbackFor = CommonException.class)
    public RouteE addRoute(RouteE routeE) {
        if (routeE.getBuiltIn() == null) {
            routeE.setBuiltIn(false);
        }
        RouteDTO routeDTO = ConvertHelper.convert(routeE, RouteDTO.class);
        try {
            int isInsert = routeMapper.insert(routeDTO);
            if (isInsert != 1) {
                throw new CommonException("error.insert.route");
            }
            modifyRouteFromGoRegister(routeDTO, ADD_ZUUL_ROOT_URL, "error to add route to register server");
        } catch (DuplicateKeyException e) {
            if (routeMapper.selectCount(new RouteDTO(routeE.getName())) > 0) {
                throw new CommonException("error.route.insert.nameDuplicate");
            } else {
                throw new CommonException("error.route.insert.pathDuplicate");
            }
        }
        return ConvertHelper.convert(routeMapper.selectByPrimaryKey(routeDTO.getId()), RouteE.class);
    }


    @Override
    @RouteNotifyRefresh
    @Transactional(rollbackFor = CommonException.class)
    public RouteE updateRoute(RouteE routeE) {
        RouteDTO oldRouteD = routeMapper.selectByPrimaryKey(routeE.getId());
        if (oldRouteD == null) {
            throw new CommonException("error.route.not.exist");
        }
        if (oldRouteD.getBuiltIn()) {
            throw new CommonException("error.route.updateBuiltIn");
        }
        RouteDTO routeDTO = ConvertHelper.convert(routeE, RouteDTO.class);
        if (routeDTO.getObjectVersionNumber() == null) {
            throw new CommonException("error.objectVersionNumber.empty");
        }
        routeDTO.setBuiltIn(null);
        try {
            int isUpdate = routeMapper.updateByPrimaryKeySelective(routeDTO);
            if (isUpdate != 1) {
                throw new CommonException("error.update.route");
            }
            modifyRouteFromGoRegister(routeDTO, ADD_ZUUL_ROOT_URL, "error to update route to register server");
        } catch (DuplicateKeyException e) {
            if (routeE.getName() != null && routeMapper.selectCount(new RouteDTO(routeE.getName())) > 0) {
                throw new CommonException("error.route.insert.nameDuplicate");
            } else {
                throw new CommonException("error.route.insert.pathDuplicate");
            }
        }
        return ConvertHelper.convert(routeMapper.selectByPrimaryKey(routeE.getId()), RouteE.class);
    }


    @Override
    @RouteNotifyRefresh
    public boolean deleteRoute(RouteE routeE) {
        RouteDTO routeDTO = ConvertHelper.convert(routeE, RouteDTO.class);
        int isDelete = routeMapper.delete(routeDTO);
        if (isDelete != 1) {
            throw new CommonException("error.delete.route");
        }
        return true;
    }

    @Override
    public List<RouteE> getAllRoute() {
        List<RouteDTO> routeDTOList = routeMapper.selectAll();
        return ConvertHelper.convertList(routeDTOList, RouteE.class);
    }

    @Override
    public List<RouteE> addRoutesBatch(List<RouteE> routeEList) {
        return routeEList.stream().map(this::addRoute).collect(Collectors.toList());
    }

    @Override
    public PageInfo<RouteDTO> pageAllRoutes(int page, int size, RouteDTO routeDTO, String params) {
        return PageHelper.startPage(page,size).doSelectPageInfo(()->routeMapper.selectRoutes(routeDTO, params));
    }

    @Override
    public int countRoute(RouteDTO routeDTO) {
        return routeMapper.selectCount(routeDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long routeId) {
        RouteDTO route = routeMapper.selectByPrimaryKey(routeId);
        if (route == null) {
            throw new CommonException("error.route.not.existed", routeId);
        }
        if (route.getBuiltIn()) {
            throw new CommonException("error.builtIn.route.can.not.delete");
        }
        routeMapper.deleteByPrimaryKey(routeId);
        modifyRouteFromGoRegister(route, DELETE_ZUUL_ROOT_URL, "error to delete route from register server");
    }

    private void modifyRouteFromGoRegister(RouteDTO routeDTO, String suffix, String message) {
        String zuulRootUrl = getZuulRootUrl(suffix);
        try {
            restTemplate.postForEntity(zuulRootUrl, routeDTO, Void.class);
        } catch (Exception e) {
            throw new CommonException(message);
        }
    }

    private String getZuulRootUrl(String suffix) {
        if (!registerUrl.endsWith("/eureka") && !registerUrl.endsWith("/eureka/")) {
            throw new CommonException("error.illegal.register-service.url");
        }
        String[] array = registerUrl.split("/eureka");
        String registerHost = array[0];
        return registerHost + suffix;
    }
}
