package io.choerodon.manager.app.service.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.core.convertor.ConvertHelper
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.app.service.SwaggerService
import io.choerodon.manager.domain.manager.entity.SwaggerE
import io.choerodon.manager.domain.service.ISwaggerService
import io.choerodon.manager.infra.dto.SwaggerDTO
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification
import springfox.documentation.swagger.web.SwaggerResource

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class SwaggerServiceImplSpec extends Specification {

    private ObjectMapper objectMapper = new ObjectMapper()

    private ISwaggerService mockISwaggerService = Mock(ISwaggerService)

    private SwaggerService swaggerService = new SwaggerServiceImpl(mockISwaggerService)

    def "GetSwaggerResource"() {
        given: "构造ISwaggerService参数"
        List<SwaggerResource> swaggerResources = new ArrayList<>();
        String swaggerResourcesJson = '[{"name":"manager","location":"shanghai","swaggerVersion":"1"},{"name":"manager","location":"shanghai","swaggerVersion":"2"}]'
        swaggerResources = objectMapper.readValue(swaggerResourcesJson, new TypeReference<List<SwaggerResource>>() {})

        when: "调用获取swagger服务列表接口"
        def list = swaggerService.getSwaggerResource()

        then: "调用getSwaggerResource方法且返回List不为空"
        1 * mockISwaggerService.getSwaggerResource() >> { swaggerResources }
        0 * _
        !list.isEmpty()
    }

    def "GetUiConfiguration"() {
        when: "调用获取swagger服务security配置"
        swaggerService.getUiConfiguration()

        then: "校验状态码和调用次数"
        1 * mockISwaggerService.getUiConfiguration()
        0 * _
    }

    def "GetSecurityConfiguration"() {
        when: "调用获取swagger服务ui配置"
        swaggerService.getSecurityConfiguration()

        then: "校验状态码和调用次数"
        1 * mockISwaggerService.getSecurityConfiguration()
        0 * _
    }

    def "测试SwaggerConverter转换器"() {
        given:
        def swaggerDO = new SwaggerDTO()
        swaggerDO.setServiceName("test-service")

        when:
        def convertSwaggerE = ConvertHelper.convert(swaggerDO, SwaggerE)
        def convertSwaggerDO = ConvertHelper.convert(convertSwaggerE, SwaggerDTO)

        then:
        noExceptionThrown()
        convertSwaggerDO!=null
    }
}
