package io.choerodon.manager.api.controller.v1

import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.app.service.DocumentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.remoting.RemoteAccessException
import org.springframework.util.concurrent.SettableListenableFuture
import spock.lang.Shared
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
    private DocumentService documentService
    @Autowired
    KafkaTemplate<byte[], byte[]> kafkaTemplate
    @Autowired
    DocumentController documentController
    @Shared
    Map<String, String> info
    @Shared
    Map<String, String> info2
    @Shared
    String serviceName
    @Shared
    String version

    void setupSpec() {
        info = new HashMap<String, String>()
        info.put("version", "")
        info2 = new HashMap<String, String>()
        info2.put("version", "test_version")
        serviceName = 'test_notExist'
        version = 'test_version'
    }

    def "Get"() {
        given: 'mock'
        def documentService = Stub(DocumentService)
        documentService.getSwaggerJson(_, _) >> { return "test" }
        documentController.setDocumentService(documentService)
        when: '向【获取服务id对应的版本swagger json字符串】接口发送请求'
        def entity = restTemplate.getForEntity('/docs/' + serviceName + '?version=' + 'test_version', String)
        then: '结果分析'
        entity.statusCode.is2xxSuccessful()
    }

    def "Get[404]"() {
        when: '向【获取服务id对应的版本swagger json字符串】接口发送请求'
        def entity = restTemplate.getForEntity('/docs/' + serviceName, String)
        then: '结果分析'
        entity.statusCode.is4xxClientError()
    }

    def "Get[Exception]"() {
        given: 'mock'
        def documentService = Stub(DocumentService)
        documentService.getSwaggerJson(_, _) >> { throw new IOException("") }
        documentController.setDocumentService(documentService)
        when: '向【获取服务id对应的版本swagger json字符串】接口发送请求'
        def entity = restTemplate.getForEntity('/docs/' + serviceName + '?version=' + 'test_version', String)
        then: '结果分析'
        entity.statusCode.is4xxClientError()
    }

    def "Refresh"() {
        given: 'mock kafkaTemplate的send'
        kafkaTemplate.send(_, _) >> new SettableListenableFuture<SendResult<byte[], byte[]>>()
        when: '向【手动刷新表中swagger json和权限】接口发送请求'
        HttpEntity<Object> httpEntity = new HttpEntity<>()
        def entity = restTemplate.exchange('/docs/permission/refresh/' + serviceName + '?version=' + version, HttpMethod.PUT, httpEntity, String)
        then: '结果分析'
        entity.statusCode.is2xxSuccessful()
    }

    def "Refresh[Exception]"() {
        given: 'mock kafkaTemplate的send'
        kafkaTemplate.send(_, _) >> new SettableListenableFuture<SendResult<byte[], byte[]>>()
        def documentService = Stub(DocumentService)
        documentService.manualRefresh(_, _) >> { throw new RemoteAccessException("") }
        documentController.setDocumentService(documentService)
        when: '向【手动刷新表中swagger json和权限】接口发送请求'
        HttpEntity<Object> httpEntity = new HttpEntity<>()
        def entity = restTemplate.exchange('/docs/permission/refresh/' + serviceName + '?version=' + version, HttpMethod.PUT, httpEntity, String)
        then: '结果分析'
        entity.statusCode.is4xxClientError()
    }
}
