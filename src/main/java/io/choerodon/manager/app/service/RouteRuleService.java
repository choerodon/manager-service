package io.choerodon.manager.app.service;


import com.github.pagehelper.PageInfo;
import io.choerodon.manager.api.dto.RouteRuleVO;
import org.springframework.data.domain.Pageable;


/**
 * RouteRuleService
 *
 * @author pengyuhua
 * @date 2019/10/25
 */
public interface RouteRuleService {
    /**
     * 分页查询路由规则信息
     *
     * @param pageable        分页信息
     * @param code            路由编码
     * @return                路由信息列表
     */
    PageInfo<RouteRuleVO> listRouteRules(Pageable pageable, String code);

    /**
     * 根据ID查询路由的详细信息
     *
     * @param id
     * @return
     */
    RouteRuleVO queryRouteRuleDetailById(Long id);

    /**
     * 添加路由规则信息
     *
     * @param routeRuleVO    路由规则DTO
     * @return                添加成功返回添加成功的routeRuleDTO
     */
    RouteRuleVO createRouteRule(RouteRuleVO routeRuleVO);

    /**
     * 根据路由ID删除路由信息
     *
     * @param id   路由ID
     * @return     操作结果 bool值
     */
    Boolean deleteRouteRuleById(Long id);

    /**
     * 路由规则信息更新
     *
     * @param id              更新路由id
     * @param routeRuleVO     更新路由信息
     * @return                更新完成路由规则信息
     */
    RouteRuleVO updateRouteRule(Long id, RouteRuleVO routeRuleVO);

    /**
     * 路由code重复校验
     *
     * @param code         校验信息
     * @return             校验结果
     */
    Boolean checkCode(String code);
}
