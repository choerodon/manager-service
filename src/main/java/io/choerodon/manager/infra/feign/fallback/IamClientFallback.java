package io.choerodon.manager.infra.feign.fallback;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.MenuDTO;
import io.choerodon.manager.api.dto.RouteRuleDTO;
import io.choerodon.manager.api.dto.RouteRuleVO;
import io.choerodon.manager.infra.dto.RouteDTO;
import io.choerodon.manager.infra.feign.IamClient;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * iam service feign失败回调函数
 *
 * @author superlee
 * @since 2019-06-11
 */
@Component
public class IamClientFallback implements IamClient {
    @Override
    public ResponseEntity<List<MenuDTO>> list() {
        throw new CommonException("error.iam.menu.list.failed");
    }

    @Override
    public List<RouteDTO> selectRoute(String name) {
        throw new CommonException("error.iam.route.list.failed");
    }

    @Override
    public ResponseEntity<PageInfo<RouteRuleVO>> listRouteRules(Pageable pageable, String code) {
        return null;
    }

    @Override
    public ResponseEntity<RouteRuleVO> queryRouteRuleDetailById(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<RouteRuleVO> insertRouteRule(RouteRuleVO routeRuleVO) {
        return null;
    }

    @Override
    public ResponseEntity<Boolean> deleteRouteRuleById(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<RouteRuleVO> updateRouteRule(RouteRuleVO routeRuleVO, Long objectVersionNumber) {
        return null;
    }

    @Override
    public ResponseEntity<Boolean> checkCode(RouteRuleDTO routeRuleDTO) {
        return null;
    }
}
