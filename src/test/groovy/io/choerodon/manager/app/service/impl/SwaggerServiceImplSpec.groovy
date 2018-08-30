package io.choerodon.manager.app.service.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.domain.service.ISwaggerService
import io.choerodon.manager.domain.service.impl.ISwaggerServiceImpl
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification
import spock.mock.DetachedMockFactory
import springfox.documentation.swagger.web.SwaggerResource

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class SwaggerServiceImplSpec extends Specification {

    private ObjectMapper objectMapper = new ObjectMapper()

    @Autowired
    private SwaggerServiceImpl swaggerService

    private ISwaggerService iSwaggerService = Mockito.mock(ISwaggerServiceImpl)

    def "GetSwaggerResource"() {
        given: "构造ISwaggerService"
        //def iSwaggerService = Spy(ISwaggerServiceImpl, constructorArgs: [null])
        List<SwaggerResource> swaggerResources = new ArrayList<>();
        String swaggerResourcesJson = '[{"name":"manager","location":"shanghai","swaggerVersion":"1"},{"name":"manager","location":"shanghai","swaggerVersion":"2"}]'
        swaggerResources = objectMapper.readValue(swaggerResourcesJson, new TypeReference<List<SwaggerResource>>() {})
        //iSwaggerService.getSwaggerResource() >> { swaggerResources }
        Mockito.doReturn(swaggerResources).when(iSwaggerService).getSwaggerResource()
        swaggerService.setService(iSwaggerService)

        when: "调用"
        def list = swaggerService.getSwaggerResource()

        then: "返回List不为空"
        noExceptionThrown()
        !list.isEmpty()
    }

    def "GetUiConfiguration"() {
    }

    def "GetSecurityConfiguration"() {
    }
}
