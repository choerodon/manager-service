package io.choerodon.manager.app.service.impl

import com.netflix.appinfo.InstanceInfo
import com.netflix.appinfo.LeaseInfo
import io.choerodon.core.exception.CommonException
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.api.dto.InstanceDTO
import io.choerodon.manager.app.service.InstanceService
import io.choerodon.manager.infra.dataobject.Sort
import io.choerodon.manager.infra.feign.ConfigServerClient
import io.choerodon.manager.infra.mapper.ConfigMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.client.DefaultServiceInstance
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author Eugen
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class InstanceServiceImplSpec extends Specification {
    @Autowired
    InstanceService instanceService

    private RestTemplate restTemplate = Mock(RestTemplate)
    private ConfigServerClient mockConfigServerClient = Mock(ConfigServerClient)

    private DiscoveryClient mockDiscoveryClient = Mock(DiscoveryClient)

    private ConfigMapper mockConfigMapper = Mock(ConfigMapper)

    def setup() {
        instanceService = new InstanceServiceImpl(mockConfigServerClient, mockDiscoveryClient, mockConfigMapper)
        instanceService.setRestTemplate(restTemplate)
    }

    def "Query"() {
        given: '创建参数'
        def instanceDTO = new InstanceDTO("test_server:test_ip:test_port", null, null, null, null, null)
        def instanceId = instanceDTO.getInstanceId()
        def wrongInstanceId = 'test_server:wrong'
        def service = 'test_service'
        def serviceInstance = new DefaultServiceInstance("", "", 1, true)

        def serviceList = new ArrayList<String>()
        serviceList.add(service)
        def serviceInstanceList = new ArrayList<ServiceInstance>()
        serviceInstanceList.add(serviceInstance)

        when: '根据instanceId查询Instance-illegal'
        instanceService.query(wrongInstanceId)

        then: '结果分析'
        def illegalInstanceId = thrown(CommonException)
        illegalInstanceId.message == "error.illegal.instanceId"

        when: '根据instanceId查询Instance'
        mockDiscoveryClient.getServices() >> { return serviceList }
        mockDiscoveryClient.getInstances(_) >> { return serviceInstanceList }
        instanceService.query(instanceId)
        then: '分析结果'
        noExceptionThrown()
    }

    def "fetchEnvInfo[illegalURL]"() {
        given: '创建参数'
        def instanceDTO = new InstanceDTO("test_server:test_ip:test_port", null, null, null, null, null, null)
        def instanceId = instanceDTO.getInstanceId()
        def service = 'test_service'
        def wrongUrlInstanceInfo = new InstanceInfo(instanceId: "test_ip:test_server:test_port", healthCheckUrl: "wrong://111.111.11.111:1111/")
        def leaseInfo = new LeaseInfo(registrationTimestamp: 1L)
        wrongUrlInstanceInfo.setLeaseInfo(leaseInfo)
        def serviceInstance = new EurekaDiscoveryClient.EurekaServiceInstance(wrongUrlInstanceInfo)
        def serviceList = new ArrayList<String>()
        serviceList.add(service)
        def serviceInstanceList = new ArrayList<ServiceInstance>()
        serviceInstanceList.add(serviceInstance)
        when: '根据instanceId查询Instance-urlIllegal'
        mockDiscoveryClient.getServices() >> { return serviceList }
        mockDiscoveryClient.getInstances(_) >> { return serviceInstanceList }
        instanceService.query(instanceId)
        then: '分析结果'
        def fetchEnv = thrown(CommonException)
        fetchEnv.message == "error.illegal.management.url"
    }

    def "fetchEnvInfo[can not fetch env info]"() {
        given: '创建参数'
        def instanceId = 'test_server:test_ip:test_port'
        def service = 'test_service'
        def instanceInfo = new InstanceInfo(instanceId: "test_ip:test_server:test_port", healthCheckUrl: "http://111.111.11.111:1111/")
        def leaseInfo = new LeaseInfo(registrationTimestamp: 1L)
        instanceInfo.setLeaseInfo(leaseInfo)
        def serviceInstance = new EurekaDiscoveryClient.EurekaServiceInstance(instanceInfo)

        def serviceList = new ArrayList<String>()
        serviceList.add(service)
        def serviceInstanceList = new ArrayList<ServiceInstance>()
        serviceInstanceList.add(serviceInstance)

        when: '根据instanceId查询Instance'
        mockDiscoveryClient.getServices() >> { return serviceList }
        mockDiscoveryClient.getInstances(_) >> { return serviceInstanceList }
        instanceService.query(instanceId)
        then: '分析结果'
        def fetchEnv = thrown(CommonException)
        fetchEnv.message == "error.config.fetchEnv"
    }

    def "fetchEnvInfo[HttpStatus.NOT_FOUND]"() {
        given: '创建参数'
        def instanceId = 'test_server:test_ip:test_port'
        def service = 'test_service'
        def instanceInfo = new InstanceInfo(instanceId: "test_ip:test_server:test_port", healthCheckUrl: "http://111.111.11.111:1111/")
        def leaseInfo = new LeaseInfo(registrationTimestamp: 1L)
        instanceInfo.setLeaseInfo(leaseInfo)
        def serviceInstance = new EurekaDiscoveryClient.EurekaServiceInstance(instanceInfo)

        def serviceList = new ArrayList<String>()
        serviceList.add(service)
        def serviceInstanceList = new ArrayList<ServiceInstance>()
        serviceInstanceList.add(serviceInstance)

        and: 'mock'
        def response = new ResponseEntity<String>(HttpStatus.NOT_FOUND)
        restTemplate.getForEntity(_, _) >> { return response }

        when: '根据instanceId查询Instance'
        mockDiscoveryClient.getServices() >> { return serviceList }
        mockDiscoveryClient.getInstances(_) >> { return serviceInstanceList }
        instanceService.query(instanceId)
        then: '分析结果'
        def fetchEnv = thrown(CommonException)
        fetchEnv.message == "error.config.fetchEnv"
    }

    def "processEnvJson"() {
        given: '创建参数'
        def instanceId = 'test_server:test_ip:test_port'
        def service = 'test_service'
        def instanceInfo = new InstanceInfo(instanceId: "test_ip:test_server:test_port", healthCheckUrl: "http://111.111.11.111:1111/")
        def leaseInfo = new LeaseInfo(registrationTimestamp: 1L)
        instanceInfo.setLeaseInfo(leaseInfo)
        def serviceInstance = new EurekaDiscoveryClient.EurekaServiceInstance(instanceInfo)

        def serviceList = new ArrayList<String>()
        serviceList.add(service)
        def serviceInstanceList = new ArrayList<ServiceInstance>()
        serviceInstanceList.add(serviceInstance)
        def response = new ResponseEntity<String>('{"testBody":{"testWithin":"testWithin"},"server.ports":"11111","local.server.port":"11111","local.management.port":"1111","random": {"profilesWithin":"profilesWithin"}}', HttpStatus.OK)


        and: 'mock'
        restTemplate.getForEntity(_, _) >> { return response }

        when: '根据instanceId查询Instance'
        mockDiscoveryClient.getServices() >> { return serviceList }
        mockDiscoveryClient.getInstances(_) >> { return serviceInstanceList }
        instanceService.query(instanceId)

        then: '分析结果'
        noExceptionThrown()
    }

    def "Update"() {
        given: '准备参数'
        def instanceId = 'test_server:test_ip:test_port'
        def wrongInstanceId = 'test_server:wrong'
        def configId = 1L
        def configVersion = new ArrayList<String>()
        configVersion.add("")

        when: '更新实例-badParameter'
        instanceService.update(wrongInstanceId, configId)
        then: '结果分析'
        def badParameter = thrown(CommonException)
        badParameter.message == "error.instance.updateConfig.badParameter"

        when: '更新实例'
        mockConfigMapper.selectConfigVersionById(_) >> { return configVersion }
        mockConfigServerClient.refresh(_) >> { return "" }
        instanceService.update(instanceId, configId)
        then: '结果分析'
        noExceptionThrown()
    }

    def "ListByOptions"() {
        given: '准备参数'
        def service = "test_service"
        def map = new HashMap<String, Object>()

        def serviceList = new ArrayList<String>()
        serviceList.add(service)

        def instanceInfo = new InstanceInfo(appName: "go-register-server", instanceId: "test_ip:test_server:test_port", healthCheckUrl: "http://111.111.11.111:1111/")
        def instanceInfo1 = new InstanceInfo(appName: "manager-server", instanceId: "test_ip:test_server:test_port", healthCheckUrl: "http://111.111.11.111:1111/")
        def leaseInfo = new LeaseInfo(registrationTimestamp: 1L)
        instanceInfo.setLeaseInfo(leaseInfo)
        instanceInfo1.setLeaseInfo(leaseInfo)
        def serviceInstance = new EurekaDiscoveryClient.EurekaServiceInstance(instanceInfo)
        def serviceInstance1 = new EurekaDiscoveryClient.EurekaServiceInstance(instanceInfo1)
        def serviceInstanceList = new ArrayList<ServiceInstance>()
        serviceInstanceList.add(serviceInstance)
        serviceInstanceList.add(serviceInstance1)

        and: "构造pageRequest"
        def order = new Sort.Order("id")
        Sort sort = new Sort(order)

        and: 'mock'
        mockDiscoveryClient.getServices() >> { return serviceList }
        mockDiscoveryClient.getInstances(_) >> { return serviceInstanceList }

        when: '查询实例列表'
        instanceService.listByOptions(service, map, 1, 20, sort)
        then: '结果分析'
        noExceptionThrown()

        when: '查询实例列表'
        instanceService.listByOptions("", map, 1, 20, sort)
        then: '结果分析'
        noExceptionThrown()
    }
}
