package io.choerodon.manager.app.service.impl

import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.app.service.DocumentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.util.concurrent.SettableListenableFuture
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
    DocumentService documentService
    @Autowired
    KafkaTemplate<byte[], byte[]> kafkaTemplate
    @Shared
    String name
    @Shared
    String version

    void setupSpec() {
        name = "test_service"
        version = 'v1'

    }

    def "GetSwaggerJson"() {
//        given:
//        def discoveryClient = Spy(DiscoveryClient)
//        discoveryClient.getServices() >> { ["test-service"] }
        when: '测试方法'
        def json = documentService.getSwaggerJson(name, version)
        printf json
        then: '结果分析'
        noExceptionThrown()
    }

    def "ManualRefresh"() {
        given: 'mock kafkaTemplate的send'
        kafkaTemplate.send(_, _) >> new SettableListenableFuture<SendResult<byte[], byte[]>>()
        when: '测试方法'
        documentService.manualRefresh(name, version)
        then: '结果分析'
        noExceptionThrown()
    }
}
