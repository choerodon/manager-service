package io.choerodon.manager.domain.service.impl

import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.domain.manager.entity.RouteE
import io.choerodon.manager.domain.repository.RouteRepository
import io.choerodon.manager.domain.service.IRouteService
import io.choerodon.manager.infra.dataobject.RouteDO
import org.apache.commons.collections.map.MultiKeyMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.context.annotation.Import
import org.springframework.remoting.RemoteAccessException
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author superlee
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class IRouteServiceImplSpec extends Specification {

    private RouteRepository mockRouteRepository = Mock(RouteRepository)

    @Autowired
    private DiscoveryClient mockDiscoveryClient

    private IRouteService iRouteService

    def setup() {
        iRouteService = new IRouteServiceImpl(mockRouteRepository, mockDiscoveryClient)
    }

    def "PageAll"() {
        given: "构建请求对象"
        def routeDO = new RouteDO()
        def params = "params"

        when: "调用创建routeDTO方法"
        iRouteService.pageAll(0, 10, routeDO, params)

        then: "校验调用次数"
        1 * mockRouteRepository.pageAllRoutes(_, _, _ as RouteDO, params)
        0 * _
    }

    def "AddRoutes"() {
        given: "构建RouteEList"
        def routeEList = new ArrayList<RouteE>()
        def routeE = new RouteE()
        routeE.setName("manager")
        routeE.setPath("/manager/**")
        routeE.setServiceId("manager-service")
        routeEList.add(routeE)

        when: "调用addRoutes"
        iRouteService.addRoutes(routeEList)

        then: "校验调用次数"
        1 * mockRouteRepository.addRoutesBatch(routeEList)
        0 * _
    }

    def "GetAll"() {
        when: "调用GetAll"
        iRouteService.getAll()

        then: "校验调用次数"
        1 * mockRouteRepository.getAllRoute()
        0 * _
    }

    def "GetAllRunningInstances"() {
        given: "构建RouteEList"
        def routeEList = new ArrayList<RouteE>()
        def routeE = new RouteE()
        routeE.setName("manager")
        routeE.setPath("/manager/**")
        routeE.setServiceId("manager-service")
        routeEList.add(routeE)

        when: "调用测试"
        def multiKeyMap = iRouteService.getAllRunningInstances()

        then: "返回的map不为空,校验调用次数"
        1 * mockRouteRepository.getAllRoute() >> { routeEList }
        0 * _
        !multiKeyMap.isEmpty()
    }

    def "GetRouteFromRunningInstancesMap"() {
        given: "构造请求参数"
        def runningMap = new MultiKeyMap()
        def routeE = new RouteE()
        routeE.setName("manager")
        runningMap.put("key1", "key2", routeE)
        def name = "manager"
        def version = "1.0"

        when: "调用GetRouteFromRunningInstancesMap函数"
        def returnRouteE = iRouteService.getRouteFromRunningInstancesMap(runningMap, name, version)

        then: "校验结果"
        returnRouteE.getName().equals(name)
    }

    def "AutoRefreshRoute"() {
        given: "创建swaggerJson"
        def swaggerJson = '{"extraData":{"data":{"choerodon_route":{"name":"test","path":"/test/**","serviceId":"manager-service"}}}}'
        def updateSwaggerJson = '{"extraData":{"data":{"choerodon_route":{"name":"manager","path":"/manager/**","serviceId":"manager-service"}}}}'
        and: "构建routeE"
        def routeE = new RouteE()
        routeE.setName("manager")
        routeE.setPath("/manager/**")

        when: "调用AutoRefreshRoute"
        iRouteService.autoRefreshRoute(swaggerJson)
        iRouteService.autoRefreshRoute(updateSwaggerJson)

        then: "校验"
        mockRouteRepository.queryRoute({ RouteE r -> "manager".equals(r.getName()) }) >> { routeE }
        noExceptionThrown()
    }

    def "FetchRouteData"() {
        given: "构造请求参数"
        def service = "manager-service"
        def version = "null_version"

        when: "调用 FetchRouteData"
        iRouteService.fetchRouteData(service, version)

        then: "校验,不能连接远程，抛出异常"
        thrown(RemoteAccessException)
    }

    def "QueryRouteByService"() {
        given: "构造请求参数"
        def service = "manager"

        when: "调用QueryRouteByService"
        iRouteService.queryRouteByService(service)

        then: "校验调用次数"
        1 * mockRouteRepository.queryRoute(_ as RouteE)
        0 * _
    }
}
