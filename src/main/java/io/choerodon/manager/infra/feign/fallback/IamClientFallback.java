package io.choerodon.manager.infra.feign.fallback;

import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.MenuDTO;
import io.choerodon.manager.infra.dto.RouteDTO;
import io.choerodon.manager.infra.feign.IamClient;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * iam service feign失败回调函数
 *
 * @author superlee
 * @since 2019-06-11
 */
public class IamClientFallback implements IamClient {
    @Override
    public ResponseEntity<List<MenuDTO>> list() {
        throw new CommonException("error.iam.menu.list.failed");
    }

    @Override
    public List<RouteDTO> selectRoute(String name) {
        throw new CommonException("error.iam.route.list.failed");
    }
}
