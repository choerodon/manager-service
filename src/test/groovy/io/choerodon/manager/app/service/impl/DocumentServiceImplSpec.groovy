package io.choerodon.manager.app.service.impl

import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.app.service.DocumentService
import io.choerodon.manager.domain.service.IDocumentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
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

    private IDocumentService mockIDocumentService = Mock(IDocumentService)

    def setup() {
        documentService = new DocumentServiceImpl(mockIDocumentService)
    }

    def "GetSwaggerJson"() {
        given: '准备参数'
        def name = "test_service"
        def version = "test_version"

        when: '测试方法'
        documentService.getSwaggerJson(name, version)

        then: '结果分析'
        noExceptionThrown()
        1 * mockIDocumentService.getSwaggerJson(name, version)
    }

}
