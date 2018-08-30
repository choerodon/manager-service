package io.choerodon.manager.app.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.appinfo.InstanceInfo
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.mybatis.pagehelper.domain.PageRequest
import io.choerodon.mybatis.pagehelper.domain.Sort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient
import org.springframework.context.annotation.Import
import spock.lang.Shared
import spock.lang.Specification
import spock.mock.DetachedMockFactory

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ServiceServiceImplSpec extends Specification {

    private ObjectMapper objectMapper = new ObjectMapper()

    private final detachedMockFactory = new DetachedMockFactory()

    @Autowired
    private ServiceServiceImpl serviceService

    def "List"() {
        when: "调用list param为空"
        def list = serviceService.list("")

        then: "返回List不为空"
        !list.isEmpty()

        when: "调用list param不为空"
        list = serviceService.list("manager")

        then: "返回List不为空"
        !list.isEmpty()
    }

    def "PageManager"() {
        given: "构造pageRequest"
        def order = new Sort.Order("id")
        def pageRequest = new PageRequest(0, 10, new Sort(order))
        and: "构造DiscoveryClient"
        def discoveryClient = Spy(DiscoveryClient)
        discoveryClient.getServices() >> { ["manager-service"] }
        String instanceJson = '{"instanceId":"localhost:manager-service:8963","app":"MANAGER-SERVICE","appGroupName":null,"ipAddr":"172.31.176.1","sid":"na","homePageUrl":"http://172.31.176.1:8963/","statusPageUrl":"http://172.31.176.1:8964/info","healthCheckUrl":"http://172.31.176.1:8964/health","secureHealthCheckUrl":null,"vipAddress":"manager-service","secureVipAddress":"manager-service","countryId":1,"dataCenterInfo":{"@class":"com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo","name":"MyOwn"},"hostName":"172.31.176.1","status":"UP","leaseInfo":{"renewalIntervalInSecs":1,"durationInSecs":3,"registrationTimestamp":1533216528607,"lastRenewalTimestamp":1533216528607,"evictionTimestamp":0,"serviceUpTimestamp":1533216528100},"isCoordinatingDiscoveryServer":false,"metadata":{},"lastUpdatedTimestamp":1533216528607,"lastDirtyTimestamp":1533208711227,"actionType":"ADDED","asgName":null,"overriddenStatus":"UNKNOWN"}'
        def instanceInfo = objectMapper.readValue(instanceJson, InstanceInfo)
        def eurekaServiceInstance = new EurekaDiscoveryClient.EurekaServiceInstance(instanceInfo)
        def serviceInstance = (ServiceInstance) eurekaServiceInstance
        def serviceInstances = new ArrayList<ServiceInstance>()
        serviceInstances << serviceInstance
        discoveryClient.getInstances(_) >> { serviceInstances }
        serviceService.setDiscoveryClient(discoveryClient)

        when: "调用"
        def list = serviceService.pageManager("manager-service", "manager", pageRequest)

        then: "返回List不为空"
        !list.isEmpty()
    }
}
