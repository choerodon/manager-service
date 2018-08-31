package io.choerodon.manager.domain.service.impl

import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.api.dto.RegisterInstancePayload
import io.choerodon.manager.domain.service.IDocumentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
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
class IDocumentServiceImplSpec extends Specification {
    @Autowired
    IDocumentService iDocumentService
    @Shared
    String service
    @Autowired
    KafkaTemplate<byte[], byte[]> kafkaTemplate
    @Shared
    String version

    void setupSpec() {
        service = "test_service"
        version = "test_version"
    }

    def "GetSwaggerJsonByIdAndVersion"() {
        when: '调用方法'
        iDocumentService.getSwaggerJsonByIdAndVersion(service, version)
        then: '结果分析'
        def error = thrown(RemoteAccessException)
        error.message == 'fetch swagger json failed'
    }

    def "FetchSwaggerJsonByService"() {
        when: '调用方法'
        iDocumentService.fetchSwaggerJsonByService(service, version)
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
        given: 'mock kafkaTemplate的send'
        kafkaTemplate.send(_, _) >> new SettableListenableFuture<SendResult<byte[], byte[]>>()
        when: ''
        iDocumentService.manualRefresh(service, version)
        then: ''
        noExceptionThrown()
    }
}
