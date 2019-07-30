package io.choerodon.manager.app.service.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.core.convertor.ConvertHelper
import io.choerodon.eureka.event.EurekaEventProperties
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.app.service.RouteService
import io.choerodon.manager.app.service.SwaggerService
import io.choerodon.manager.infra.dto.SwaggerDTO
import org.springframework.beans.factory.annotation.Autowired
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

    @Autowired
    private EurekaEventProperties properties
    @Autowired
    private RouteService routeService


    private SwaggerService swaggerService = new SwaggerServiceImpl("client", properties, routeService)

//    def "GetSwaggerResource"() {
//        given: "构造ISwaggerService参数"
//        List<SwaggerResource> swaggerResources = new ArrayList<>();
//        String swaggerResourcesJson = '[{"name":"manager","location":"shanghai","swaggerVersion":"1"},{"name":"manager","location":"shanghai","swaggerVersion":"2"}]'
//        swaggerResources = objectMapper.readValue(swaggerResourcesJson, new TypeReference<List<SwaggerResource>>() {})
//
//        when: "调用获取swagger服务列表接口"
//        def list = swaggerService.getSwaggerResource()
//
//        then: "调用getSwaggerResource方法且返回List不为空"
////        1 * mockISwaggerService.getSwaggerResource() >> { swaggerResources }
//        0 * _
//        !list.isEmpty()
//    }

    def "GetUiConfiguration"() {
        when: "调用获取swagger服务security配置"
        swaggerService.getUiConfiguration()

        then: "校验状态码和调用次数"
//        1 * mockISwaggerService.getUiConfiguration()
        0 * _
    }

    def "GetSecurityConfiguration"() {
        when: "调用获取swagger服务ui配置"
        swaggerService.getSecurityConfiguration()

        then: "校验状态码和调用次数"
//        1 * mockISwaggerService.getSecurityConfiguration()
        0 * _
    }
}
