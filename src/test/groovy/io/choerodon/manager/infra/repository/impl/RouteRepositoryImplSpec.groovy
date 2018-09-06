package io.choerodon.manager.infra.repository.impl

import io.choerodon.core.convertor.ConvertHelper
import io.choerodon.core.domain.Page
import io.choerodon.core.exception.CommonException
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.domain.manager.entity.RouteE
import io.choerodon.manager.domain.repository.RouteRepository
import io.choerodon.manager.infra.dataobject.RouteDO
import io.choerodon.manager.infra.mapper.RouteMapper
import io.choerodon.mybatis.pagehelper.domain.PageRequest
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.netflix.zuul.filters.Route
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
    //private RouteMapper routeMapper = Mock(RouteMapper)
    //private RouteRepository routeRepository = new RouteRepositoryImpl(routeMapper)

    @Autowired
    private RouteMapper routeMapper

    @Autowired
    private RouteRepository routeRepository

    @Shared
    private RouteE routeE
    @Shared
    private RouteE nameDuplicateRouteE
    @Shared
    private RouteE pathDuplicateRouteE

    def setup() {
        routeE = new RouteE()
        routeE.setName("test")
        routeE.setPath("/test/**")
        routeE.setServiceId("test-service")
    }

    def setupSpec() {
        nameDuplicateRouteE = new RouteE()
        nameDuplicateRouteE.setName("manager")
        nameDuplicateRouteE.setPath("/test/**")
        nameDuplicateRouteE.setServiceId("test-service")

        pathDuplicateRouteE = new RouteE()
        pathDuplicateRouteE.setName("test")
        pathDuplicateRouteE.setPath("/manager/**")
        pathDuplicateRouteE.setServiceId("test-service")
    }

    def "QueryRoute"() {
        given: "构造RouteE参数"
        def routeDO = routeMapper.selectAll().get(0)
        def routeE = ConvertHelper.convert(routeDO, RouteE)

        when: "调用queryRoute方法"
        def queryRouteE = routeRepository.queryRoute(routeE)

        then: "校验查询到的RouteE"
        queryRouteE.getName().equals(routeE.getName())
        queryRouteE.getPath().equals(routeE.getPath())
    }

    def "AddRoute"() {
        when: "正常AddRoute"
        def addRouteE = routeRepository.addRoute(routeE)
        then: "校验正常信息并删除routeE"
        addRouteE.getName().equals(routeE.getName())
        addRouteE.getPath().equals(routeE.getPath())
        routeMapper.delete(ConvertHelper.convert(addRouteE, RouteDO))

        when: "name异常AddRoute"
        routeRepository.addRoute(nameDuplicateRouteE)
        then: "校验异常信息"
        def error = thrown(CommonException)
        error.message == "error.route.insert.nameDuplicate"

        when: "path异常AddRoute"
        routeRepository.addRoute(pathDuplicateRouteE)
        then: "校验异常信息"
        error = thrown(CommonException)
        error.message == "error.route.insert.pathDuplicate"
        /*where: "异常对比"
        duplicateRouteE || expectedException | expectedMessage
        nameDuplicateRouteE || CommonException | "error.route.insert.nameDuplicate"
        pathDuplicateRouteE || CommonException | "error.route.insert.pathDuplicate"*/
    }

    def "UpdateRoute"() {
        given: "构造addRouteE"
        def addRouteE = routeRepository.addRoute(routeE)
        addRouteE.setName("testupdate")
        addRouteE.setPath("/testupdate/**")
        addRouteE.setServiceId("testupdate-service")

        when: "要更新的Route不存在"
        def errorRouteE = new RouteE()
        BeanUtils.copyProperties(addRouteE, errorRouteE)
        errorRouteE.setId(100L)
        routeRepository.updateRoute(errorRouteE)

        then: "校验异常信息"
        def error = thrown(CommonException)
        error.message == "error.route.not.exist"

        when:"要更新的Route getObjectVersionNumber为null"
        BeanUtils.copyProperties(addRouteE, errorRouteE)
        errorRouteE.setObjectVersionNumber(null)
        routeRepository.updateRoute(errorRouteE)

        then: "校验异常信息"
        error = thrown(CommonException)
        error.message == "error.objectVersionNumber.empty"

        when:"要更新的Route name重复"
        BeanUtils.copyProperties(addRouteE, errorRouteE)
        errorRouteE.setName("manager")
        routeRepository.updateRoute(errorRouteE)

        then: "校验异常信息"
        error = thrown(CommonException)
        error.message == "error.route.insert.nameDuplicate"

        when:"要更新的Route path重复"
        BeanUtils.copyProperties(addRouteE, errorRouteE)
        errorRouteE.setPath("/manager/**")
        routeRepository.updateRoute(errorRouteE)

        then: "校验异常信息"
        error = thrown(CommonException)
        error.message == "error.route.insert.pathDuplicate"

        when: "正确调用UpdateRoute"
        def updateRouteE = routeRepository.updateRoute(addRouteE)

        then: "校验更新后RouteE"
        updateRouteE.getName().equals(addRouteE.getName())
        updateRouteE.getPath().equals(addRouteE.getPath())
        routeMapper.delete(ConvertHelper.convert(updateRouteE, RouteDO))
    }

    def "DeleteRoute"() {
        given: "构造addRouteE"
        def addRouteE = routeRepository.addRoute(routeE)

        when: "调用DeleteRoute方法"
        def success = routeRepository.deleteRoute(addRouteE)

        then: "校验正常信息并删除routeE"
        success
    }

    def "GetAllRoute"() {
        when: "调用GetAllRoute"
        def retuenRouteEList = routeRepository.getAllRoute()

        then: "校验retuenRouteEList"
        !retuenRouteEList.isEmpty()
    }

    def "AddRoutesBatch"() {
        given: "构造routeEs参数"
        def routeEList = new ArrayList<RouteE>()
        def routeE1 = new RouteE()
        routeE1.setName("test1")
        routeE1.setPath("/test1/**")
        routeE1.setServiceId("test1-service")
        routeEList.add(routeE1)
        def routeE2 = new RouteE()
        routeE2.setName("test2")
        routeE2.setPath("/test2/**")
        routeE2.setServiceId("test2-service")
        routeEList.add(routeE2)

        when: "调用AddRoutesBatch"
        def retuenRouteEList = routeRepository.addRoutesBatch(routeEList)

        then: "校验并删除插入的RouteE"
        !retuenRouteEList.isEmpty()
        retuenRouteEList.size() == 2
        routeMapper.delete(ConvertHelper.convert(retuenRouteEList.get(0), RouteDO))
        routeMapper.delete(ConvertHelper.convert(retuenRouteEList.get(1), RouteDO))
    }

    def "PageAllRoutes"() {
        given: "构造RouteDO参数"
        def pageRequest = new PageRequest(0, 10)
        def routeDO = new RouteDO()
        def params = null

        when: "调用pageAllRoutes"
        Page<RouteE> page = routeRepository.pageAllRoutes(pageRequest, routeDO, params)

        then: "校验"
        !page.isEmpty()
    }

    def "CountRoute"() {
        given: "构造RouteDO参数"
        def routeDO = new RouteDO()

        when: "调用CountRoute"
        def number = routeRepository.countRoute(routeDO)

        then: "校验"
        number != 0
    }
}
