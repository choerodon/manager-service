package io.choerodon.manager.domain.service.impl

import com.netflix.appinfo.InstanceInfo
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.app.service.RouteService
import io.choerodon.manager.app.service.impl.DocumentServiceImpl
import io.choerodon.manager.infra.dto.SwaggerDTO
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
class DocumentServiceImplSpec extends Specification {
    @Autowired
    DocumentServiceImpl iDocumentService

    private RestTemplate restTemplate = Mock(RestTemplate)

    private SwaggerMapper mockSwaggerMapper = Mock(SwaggerMapper)

    private DiscoveryClient mockDiscoveryClient = Mock(DiscoveryClient)

    RouteService routeService = Mock(RouteService)


    def setup() {
        iDocumentService = new DocumentServiceImpl(mockSwaggerMapper, mockDiscoveryClient, routeService)
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
        def swaggerDO = new SwaggerDTO()
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
        String json = iDocumentService.fetchSwaggerJsonByService(service, nullVersion)
        iDocumentService.buildSwaggerJson(json)

        then: '结果分析'
        thrown(RemoteAccessException)
    }

    def "fetch[HttpStatus.NOT_FOUND]"() {
        given: '创建mock参数'
        def swaggerDO = new SwaggerDTO()
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
        String json = iDocumentService.fetchSwaggerJsonByService(service, nullVersion)
        iDocumentService.buildSwaggerJson(json)
        then: '结果分析'
        def error = thrown(RemoteAccessException)
        error.message == 'fetch failed : ' + response
    }

    def "fetch"() {
        given: '创建mock参数'
        def swaggerDO = new SwaggerDTO()
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
        String json = iDocumentService.fetchSwaggerJsonByService(service, nullVersion)
        iDocumentService.buildSwaggerJson(json)
        then: '结果分析'
        def error = thrown(RemoteAccessException)
        error.message == 'fetch swagger json failed'
    }

    def "GetSwaggerJsonByIdAndVersion"() {
        given: '创建mock参数'
        def swaggerDO = new SwaggerDTO()
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
        String json = iDocumentService.fetchSwaggerJsonByService(service, nullVersion)
        iDocumentService.buildSwaggerJson(json)
        then: '结果分析'
        noExceptionThrown()
    }

    def "expandSwaggerJson"() {
        when: '调用方法'
        def file = new File(this.class.getResource('/swagger.json').toURI())
        String json = file.getText('UTF-8')
        iDocumentService.expandSwaggerJson(service, version, json)
        then: '结果分析'
        noExceptionThrown()
    }

}
