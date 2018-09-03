package io.choerodon.manager.api.controller.v1

import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.app.service.SwaggerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class SwaggerControllerSepc extends Specification {

    @Autowired
    private TestRestTemplate restTemplate

    @Autowired
    private SwaggerController swaggerController

    private SwaggerService mockSwaggerService = Mock(SwaggerService)

    def setup() {
        swaggerController.setSwaggerService(mockSwaggerService)
    }

    def "SecurityConfiguration"() {
        when: "调用获取swagger服务security配置"
        def entity = restTemplate.getForEntity("/swagger-resources/configuration/security", String)

        then: "校验状态码和调用次数"
        entity.statusCode.is2xxSuccessful()
        1 * mockSwaggerService.getSecurityConfiguration()
        0 * _
    }

    def "UiConfiguration"() {
        when: "调用获取swagger服务ui配置"
        def entity = restTemplate.getForEntity("/swagger-resources/configuration/ui", String)

        then: "校验状态码和调用次数"
        entity.statusCode.is2xxSuccessful()
        1 * mockSwaggerService.getUiConfiguration()
        0 * _
    }

    def "SwaggerResources"() {
        when: "调用controller层获取swagger服务列表接口"
        def entity = restTemplate.getForEntity("/swagger-resources", String)

        then: "校验状态码和调用次数"
        entity.statusCode.is2xxSuccessful()
        1 * mockSwaggerService.getSwaggerResource()
        0 * _
    }
}
