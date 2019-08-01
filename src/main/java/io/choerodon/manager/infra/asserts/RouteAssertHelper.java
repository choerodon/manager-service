package io.choerodon.manager.infra.asserts;

import io.choerodon.core.exception.ext.AlreadyExistedException;
import io.choerodon.core.exception.ext.IllegalArgumentException;
import io.choerodon.core.exception.ext.NotExistedException;
import io.choerodon.manager.infra.dto.RouteDTO;
import io.choerodon.manager.infra.mapper.RouteMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * route断言类
 *
 * @author superlee
 * @since 2019-07-19
 */
@Component
public class RouteAssertHelper extends AssertHelper {

    private final RouteMapper routeMapper;

    public RouteAssertHelper(RouteMapper routeMapper) {
        this.routeMapper = routeMapper;
    }

    public RouteDTO notExisted(WhichColumn whichColumn, String value) {
        return notExisted(whichColumn, value, "error.route.not.existed");
    }

    public RouteDTO notExisted(Long id) {
        return notExisted(id, "error.route.not.existed");
    }

    public RouteDTO notExisted(Long id, String message) {
        return Optional.ofNullable(routeMapper.selectByPrimaryKey(id)).orElseThrow(() -> new NotExistedException(message));
    }

    public RouteDTO notExisted(WhichColumn whichColumn, String value, String message) {
        RouteDTO dto = new RouteDTO();
        switch (whichColumn) {
            case NAME:
                dto.setName(value);
                return Optional
                        .ofNullable(routeMapper.selectOne(dto))
                        .orElseThrow(() -> new NotExistedException(message));
            case PATH:
                dto.setPath(value);
                return Optional
                        .ofNullable(routeMapper.selectOne(dto))
                        .orElseThrow(() -> new NotExistedException(message));
            default:
                throw new IllegalArgumentException("error.illegal.argument" + whichColumn.value);
        }
    }

    public void nameExisted(String name) {
        RouteDTO dto = new RouteDTO();
        dto.setName(name);
        if (routeMapper.selectOne(dto) != null) {
            throw new AlreadyExistedException("error.route.name.existed");
        }
    }

    public void pathExisted(String path) {
        RouteDTO dto = new RouteDTO();
        dto.setPath(path);
        if (routeMapper.selectOne(dto) != null) {
            throw new AlreadyExistedException("error.route.path.existed");
        }
    }

    public enum WhichColumn {
        PATH("path"), NAME("name");
        String value;

        WhichColumn(String value) {
            this.value = value;
        }
    }
}
