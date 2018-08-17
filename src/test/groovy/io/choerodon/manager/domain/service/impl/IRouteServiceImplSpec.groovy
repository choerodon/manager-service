package io.choerodon.manager.domain.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.appinfo.InstanceInfo
import io.choerodon.manager.IntegrationTestConfiguration
import org.springframework.beans.factory.annotation.Autowired
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
        discoveryClient.getServices() >> { ["manager-service"] }
        String instanceJson = '{"instanceId":"localhost:manager-service:8963","app":"MANAGER-SERVICE","appGroupName":null,"ipAddr":"172.31.176.1","sid":"na","homePageUrl":"http://172.31.176.1:8963/","statusPageUrl":"http://172.31.176.1:8964/info","healthCheckUrl":"http://172.31.176.1:8964/health","secureHealthCheckUrl":null,"vipAddress":"manager-service","secureVipAddress":"manager-service","countryId":1,"dataCenterInfo":{"@class":"com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo","name":"MyOwn"},"hostName":"172.31.176.1","status":"UP","leaseInfo":{"renewalIntervalInSecs":1,"durationInSecs":3,"registrationTimestamp":1533216528607,"lastRenewalTimestamp":1533216528607,"evictionTimestamp":0,"serviceUpTimestamp":1533216528100},"isCoordinatingDiscoveryServer":false,"metadata":{},"lastUpdatedTimestamp":1533216528607,"lastDirtyTimestamp":1533208711227,"actionType":"ADDED","asgName":null,"overriddenStatus":"UNKNOWN"}'
        def instanceInfo = objectMapper.readValue(instanceJson, InstanceInfo)
        def eurekaServiceInstance = new EurekaDiscoveryClient.EurekaServiceInstance(instanceInfo)
        def serviceInstance = (ServiceInstance)eurekaServiceInstance
        def serviceInstances = new ArrayList<ServiceInstance>()
        serviceInstances << serviceInstance
        discoveryClient.getInstances(_) >> { serviceInstances }
        iRouteService.unitTestInit(discoveryClient)

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
