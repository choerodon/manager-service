package io.choerodon.manager.app.service.impl

import com.netflix.appinfo.InstanceInfo
import com.netflix.appinfo.LeaseInfo
import io.choerodon.core.exception.CommonException
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.app.service.InstanceService
import io.choerodon.manager.infra.feign.ConfigServerClient
import io.choerodon.manager.infra.mapper.ConfigMapper
import io.choerodon.mybatis.pagehelper.domain.PageRequest
import io.choerodon.mybatis.pagehelper.domain.Sort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient
import org.springframework.context.annotation.Import
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
    private ConfigServerClient mockConfigServerClient = Mock(ConfigServerClient)

    private DiscoveryClient mockDiscoveryClient = Mock(DiscoveryClient)

    private ConfigMapper mockConfigMapper = Mock(ConfigMapper)

    def setup() {
        instanceService = new InstanceServiceImpl(mockConfigServerClient, mockDiscoveryClient, mockConfigMapper)
    }

    def "Query"() {
        given: '创建参数'
        def instanceId = 'test_server:test_ip:test_port'
        def wrongInstanceId = 'test_server:wrong'
        def service = 'test_service'
        def instanceInfo = new InstanceInfo(instanceId: "test_ip:test_server:test_port", healthCheckUrl: "http://111.111.11.111:1111/")
        def leaseInfo = new LeaseInfo(registrationTimestamp: 1L)
        instanceInfo.setLeaseInfo(leaseInfo)
        def serviceInstance = new EurekaDiscoveryClient.EurekaServiceInstance(instanceInfo)

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
        def fetchEnv = thrown(CommonException)
        fetchEnv.message == "error.config.fetchEnv"
    }

    def "Update"() {
        given: '准备参数'
        def instanceId = 'test_server:test_ip:test_port'
        def wrongInstanceId = 'test_server:wrong'
        def configId = 1L
        def configVersion = new ArrayList<String>()
        configVersion.add("test_version")

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

        def instanceInfo = new InstanceInfo(appName: "test_app", instanceId: "test_ip:test_server:test_port", healthCheckUrl: "http://111.111.11.111:1111/")
        def leaseInfo = new LeaseInfo(registrationTimestamp: 1L)
        instanceInfo.setLeaseInfo(leaseInfo)
        def serviceInstance = new EurekaDiscoveryClient.EurekaServiceInstance(instanceInfo)
        def serviceInstanceList = new ArrayList<ServiceInstance>()
        serviceInstanceList.add(serviceInstance)

        and: "构造pageRequest"
        def order = new Sort.Order("id")
        def pageRequest = new PageRequest(1, 20, new Sort(order))

        and: 'mock'
        mockDiscoveryClient.getServices() >> { return serviceList }
        mockDiscoveryClient.getInstances(_) >> { return serviceInstanceList }

        when: '查询实例列表'
        instanceService.listByOptions(service, map, pageRequest)
        then: '结果分析'
        noExceptionThrown()

        when: '查询实例列表'
        instanceService.listByOptions("", map, pageRequest)
        then: '结果分析'
        noExceptionThrown()
    }
}
