package io.choerodon.manager.infra.repository.impl

import io.choerodon.core.convertor.ConvertHelper
import io.choerodon.core.exception.CommonException
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.domain.manager.entity.RouteE
import io.choerodon.manager.domain.repository.RouteRepository
import io.choerodon.manager.infra.dataobject.RouteDO
import io.choerodon.manager.infra.mapper.RouteMapper
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.dao.DuplicateKeyException
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class RouteRepositoryImplSpec extends Specification {

    public static final String DUPLICATE_ROUTE_NAME = "manager-service"
    public static final String DUPLICATE_ROUTE_PATH = "/manager/**"
    public static final String ERROR_ROUTE_NAME = "error-service"

    private RouteMapper routeMapper = Mock(RouteMapper)
    private RouteRepository routeRepository = new RouteRepositoryImpl(routeMapper)

    @Shared
    private RouteDO errorRouteDO
    @Shared
    private RouteDO nameDuplicateRouteDO
    @Shared
    private RouteDO pathDuplicateRouteDO

    def setupSpec() {
        nameDuplicateRouteDO = new RouteDO()
        nameDuplicateRouteDO.setName(DUPLICATE_ROUTE_NAME)

        pathDuplicateRouteDO = new RouteDO()
        pathDuplicateRouteDO.setPath(DUPLICATE_ROUTE_PATH)

        errorRouteDO = new RouteDO()
        errorRouteDO.setName(ERROR_ROUTE_NAME)
    }

    def "QueryRoute"() {
    }

    def "AddRoute"() {
        when:
        routeRepository.addRoute(ConvertHelper.convert(duplicateRouteDTO, RouteE))

        then: "校验异常信息"
        _ * routeMapper.insert({ RouteDO e -> e.getName() == DUPLICATE_ROUTE_NAME || e.getPath() == DUPLICATE_ROUTE_PATH }) >> {
            throw new DuplicateKeyException("DuplicateKeyException")
        }
        _ * routeMapper.selectCount({ RouteDO r -> r.getName() == DUPLICATE_ROUTE_NAME }) >> { 1 }
        _ * routeMapper.selectByPrimaryKey(_) >> { errorRouteDO }
        def error = thrown(expectedException)
        error.message == expectedMessage

        where: "异常对比"
        duplicateRouteDTO    || expectedException | expectedMessage
        errorRouteDO         || CommonException   | "error.insert.route"
        nameDuplicateRouteDO || CommonException   | "error.route.insert.nameDuplicate"
        pathDuplicateRouteDO || CommonException   | "error.route.insert.pathDuplicate"
    }

    def "UpdateRoute"() {
    }

    def "DeleteRoute"() {
    }

    def "GetAllRoute"() {
    }

    def "AddRoutesBatch"() {
    }

    def "PageAllRoutes"() {
    }

    def "CountRoute"() {
    }
}
