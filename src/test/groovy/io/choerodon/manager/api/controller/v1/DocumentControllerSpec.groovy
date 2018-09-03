package io.choerodon.manager.api.controller.v1

import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.app.service.DocumentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.remoting.RemoteAccessException
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author Eugen
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class DocumentControllerSpec extends Specification {
    @Autowired
    private TestRestTemplate restTemplate
    @Autowired
    private DocumentController documentController
    private DocumentService mockDocumentService = Mock(DocumentService)

    void setup() {
        documentController.setDocumentService(mockDocumentService)
    }

    def "Get"() {
        given: '准备参数'
        def servicePrefix = 'manager_service'
        def version = 'v1'

        and: 'mock getSwaggerJson方法'
        mockDocumentService.getSwaggerJson(_, _) >> { return "test" }

        when: '向【获取服务id对应的版本swagger json字符串】接口发送GET请求'
        def entity = restTemplate.getForEntity('/docs/{service_prefix}?version={version}', String, servicePrefix, version)

        then: '验证状态码成功,验证参数生效'
        entity.statusCode.is2xxSuccessful()
        1 * mockDocumentService.getSwaggerJson(servicePrefix, version)
    }

    def "Get[404]"() {
        given: '准备参数'
        def servicePrefix = 'manager_service'
        def version = 'v1'

        and: 'mock getSwaggerJson方法'
        mockDocumentService.getSwaggerJson(_, _) >> { return "" }

        when: '向【获取服务id对应的版本swagger json字符串】接口发送请求'
        def entity = restTemplate.getForEntity('/docs/{service_prefix}?version={version}', String, servicePrefix, version)

        then: '验证状态码成功'
        entity.statusCode.is4xxClientError()
    }

    def "Get[Exception]"() {
        given: '准备参数'
        def servicePrefix = 'manager_service'
        def version = 'v1'

        and: 'mock getSwaggerJson方法'
        mockDocumentService.getSwaggerJson(_, _) >> { throw new IOException("") }

        when: '向【获取服务id对应的版本swagger json字符串】接口发送请求'
        def entity = restTemplate.getForEntity('/docs/{service_prefix}?version={version}', String, servicePrefix, version)

        then: '验证状态码成功'
        entity.statusCode.is4xxClientError()
    }

    def "Refresh"() {
        given: '准备参数'
        def serviceName = 'maneger_service'
        def version = 'version'

        when: '向【手动刷新表中swagger json和权限】接口发送PUT请求'
        HttpEntity<Object> httpEntity = new HttpEntity<>()
        def entity = restTemplate.exchange('/docs/permission/refresh/{service_name}?version={version}', HttpMethod.PUT, httpEntity, String, serviceName, version)

        then: '验证状态码成功'
        entity.statusCode.is2xxSuccessful()
        1 * mockDocumentService.manualRefresh(serviceName, version)
    }

    def "Refresh[Exception]"() {
        given: '准备参数'
        def serviceName = 'maneger_service'
        def version = 'version'

        and: 'mock mockDocumentService的manualRefresh'
        mockDocumentService.manualRefresh(_, _) >> { throw new RemoteAccessException("") }

        when: '向【手动刷新表中swagger json和权限】接口发送请求'
        HttpEntity<Object> httpEntity = new HttpEntity<>()
        def entity = restTemplate.exchange('/docs/permission/refresh/{service_name}?version={version}', HttpMethod.PUT, httpEntity, String, serviceName, version)

        then: '结果分析'
        entity.statusCode.is4xxClientError()
    }
}
