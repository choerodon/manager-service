package io.choerodon.manager.domain.service.impl

import com.netflix.appinfo.InstanceInfo
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.domain.service.IRouteService
import io.choerodon.manager.domain.service.SwaggerRefreshService
import io.choerodon.manager.infra.dataobject.SwaggerDO
import io.choerodon.manager.infra.mapper.SwaggerMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.remoting.RemoteAccessException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author Eugen
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class IDocumentServiceImplSpec extends Specification {
    @Autowired
    IDocumentServiceImpl iDocumentService

    private RestTemplate restTemplate = Mock(RestTemplate)

    private SwaggerMapper mockSwaggerMapper = Mock(SwaggerMapper)

    private DiscoveryClient mockDiscoveryClient = Mock(DiscoveryClient)

    private IRouteService mockIRouteService = Mock(IRouteService)

    private SwaggerRefreshService mockSwaggerRefreshService = Mock(SwaggerRefreshService)

    def setup() {
        iDocumentService = new IDocumentServiceImpl(mockSwaggerMapper, mockDiscoveryClient, mockIRouteService, mockSwaggerRefreshService)
        iDocumentService.setRestTemplate(restTemplate)
        iDocumentService.setProfiles("default")
        iDocumentService.setClient("client")
        iDocumentService.setOauthUrl("http://localhost:8080/oauth/oauth/authorize")
    }

    @Shared
    String service
    @Shared
    String version

    void setupSpec() {
        service = "test_service"
        version = "test_version"
    }

    def "fetchSwaggerJsonByService"() {
        given: '创建mock参数'
        def swaggerDO = new SwaggerDO()
        swaggerDO.setId(1L)

        def nullVersion = "null_version"
        def instanceInfo = new InstanceInfo()
        def serviceInstance = new EurekaDiscoveryClient.EurekaServiceInstance(instanceInfo)

        def serviceInstanceList = new ArrayList<ServiceInstance>()
        serviceInstanceList.add(serviceInstance)
        and: 'mock'
        restTemplate.getForEntity(_, _) >> { throw new RestClientException("") }

        when: '调用方法'
        mockSwaggerMapper.selectOne(_) >> { return swaggerDO }
        mockDiscoveryClient.getInstances(_) >> { return serviceInstanceList }
        iDocumentService.getSwaggerJsonByIdAndVersion(service, nullVersion)

        then: '结果分析'
        def error = thrown(RemoteAccessException)
        error.message == 'fetch failed, instance:null'
    }

    def "fetch[HttpStatus.NOT_FOUND]"() {
        given: '创建mock参数'
        def swaggerDO = new SwaggerDO()
        swaggerDO.setId(1L)

        def nullVersion = "null_version"

        def instanceInfo = new InstanceInfo()
        def serviceInstance = new EurekaDiscoveryClient.EurekaServiceInstance(instanceInfo)

        def serviceInstanceList = new ArrayList<ServiceInstance>()
        serviceInstanceList.add(serviceInstance)

        def response = new ResponseEntity<String>(HttpStatus.NOT_FOUND)

        and: 'mock'
        restTemplate.getForEntity(_, _) >> { return response }

        when: '调用方法'
        mockSwaggerMapper.selectOne(_) >> { return swaggerDO }
        mockDiscoveryClient.getInstances(_) >> { return serviceInstanceList }
        iDocumentService.getSwaggerJsonByIdAndVersion(service, nullVersion)
        then: '结果分析'
        def error = thrown(RemoteAccessException)
        error.message == 'fetch failed : ' + response
    }

    def "fetch"() {
        given: '创建mock参数'
        def swaggerDO = new SwaggerDO()
        swaggerDO.setId(1L)

        def nullVersion = "null_version"

        def instanceInfo = new InstanceInfo()
        def serviceInstance = new EurekaDiscoveryClient.EurekaServiceInstance(instanceInfo)

        def serviceInstanceList = new ArrayList<ServiceInstance>()
        serviceInstanceList.add(serviceInstance)

        def response = new ResponseEntity<String>(HttpStatus.OK)

        and: 'mock'
        restTemplate.getForEntity(_, _) >> { return response }

        when: '调用方法'
        mockSwaggerMapper.selectOne(_) >> { return swaggerDO }
        mockDiscoveryClient.getInstances(_) >> { return serviceInstanceList }
        iDocumentService.getSwaggerJsonByIdAndVersion(service, nullVersion)
        then: '结果分析'
        def error = thrown(RemoteAccessException)
        error.message == 'fetch swagger json failed'
    }

    def "GetSwaggerJsonByIdAndVersion"() {
        given: '创建mock参数'
        def swaggerDO = new SwaggerDO()
        swaggerDO.setId(1L)
        swaggerDO.setValue('{"paths":"test"}')
        def nullVersion = "null_version"

        def instanceInfo = new InstanceInfo()
        def serviceInstance = new EurekaDiscoveryClient.EurekaServiceInstance(instanceInfo)

        def serviceInstanceList = new ArrayList<ServiceInstance>()
        serviceInstanceList.add(serviceInstance)

        def response = new ResponseEntity<String>(HttpStatus.OK)

        and: 'mock'
        iDocumentService.setProfiles("sit")
        restTemplate.getForEntity(_, _) >> { return response }

        when: '调用方法'
        mockSwaggerMapper.selectOne(_) >> { return swaggerDO }
        mockDiscoveryClient.getInstances(_) >> { return serviceInstanceList }
        iDocumentService.getSwaggerJsonByIdAndVersion(service, nullVersion)
        then: '结果分析'
        noExceptionThrown()
    }

    def "GetSwaggerJson"() {
        when: '调用方法'
        iDocumentService.getSwaggerJson(service, version)
        then: '结果分析'
        noExceptionThrown()
    }

    def "ManualRefresh"() {
        given: '创建mock参数'
        def swaggerDO = new SwaggerDO()
        swaggerDO.setId(1L)
        swaggerDO.setValue('{"paths":"test"}')
        def nullVersion = "null_version"

        def instanceInfo = new InstanceInfo()
        def serviceInstance = new EurekaDiscoveryClient.EurekaServiceInstance(instanceInfo)

        def serviceInstanceList = new ArrayList<ServiceInstance>()
        serviceInstanceList.add(serviceInstance)

        def response = new ResponseEntity<String>(HttpStatus.OK)

        and: 'mock'
        iDocumentService.setProfiles("sit")
        restTemplate.getForEntity(_, _) >> { return response }
        //        kafkaTemplate.send(_, _) >> new SettableListenableFuture<SendResult<byte[], byte[]>>()
        mockSwaggerMapper.selectOne(_) >> { return swaggerDO }
        mockDiscoveryClient.getInstances(_) >> { return serviceInstanceList }

        when: '调用方法'
        iDocumentService.manualRefresh(service, nullVersion)

        then: '结果分析'

        noExceptionThrown()
    }
}
