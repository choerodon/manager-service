package io.choerodon.manager.domain.service.impl

import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.domain.repository.RouteRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author superlee
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class IRouteServiceImplSpec extends Specification {
    @Autowired
    private IRouteServiceImpl iRouteService
//    @Autowired
//    private RouteRepository routeRepository
//    @Autowired
//    private DiscoveryClient discoveryClient

    def "PageAll"() {
    }

    def "AddRoutes"() {
    }

    def "GetAll"() {
    }

    def "GetAllRunningInstances"() {
        given: "设置一个discoveryClient间谍，自定义方法返回"
        def discoveryClient = Spy(DiscoveryClient)
        discoveryClient.getServices() >> { new ArrayList<String>() }
        discoveryClient.getInstances(_) >> { new ArrayList<String>() }
        def routeRepository = Spy(RouteRepository)
        routeRepository.getAllRoute() >> { new ArrayList<>() }
        iRouteService.unitTestInit(discoveryClient, routeRepository)

        when: ""
        def multiKeyMap = iRouteService.getAllRunningInstances()

        then: ""
        multiKeyMap.isEmpty()

    }

    def "GetRouteFromRunningInstancesMap"() {
    }

    def "AutoRefreshRoute"() {
    }

    def "FetchRouteData"() {
    }

    def "QueryRouteByService"() {
    }
}
