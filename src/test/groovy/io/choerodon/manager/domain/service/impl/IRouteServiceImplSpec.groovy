package io.choerodon.manager.domain.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.appinfo.InstanceInfo
import io.choerodon.manager.IntegrationTestConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author superlee
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class IRouteServiceImplSpec extends Specification {

    private ObjectMapper objectMapper = new ObjectMapper()

    @Autowired
    @Qualifier("mockDiscoveryClient")
    private DiscoveryClient discoveryClient

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

        when: "调用测试"
        def multiKeyMap = iRouteService.getAllRunningInstances()

        then: "返回的map不为空"
        !multiKeyMap.isEmpty()

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
