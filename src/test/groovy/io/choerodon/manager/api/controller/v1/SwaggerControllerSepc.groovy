package io.choerodon.manager.api.controller.v1

import io.choerodon.manager.IntegrationTestConfiguration
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

    def "SecurityConfiguration"() {
        when: "发送一个get请求"
        def entity = restTemplate.getForEntity("/swagger-resources/configuration/security", String)

        then: "状态码200，调用成功"
        entity.statusCode.is2xxSuccessful()
    }

    def "UiConfiguration"() {
        when: "发送一个get请求"
        def entity = restTemplate.getForEntity("/swagger-resources/configuration/ui", String)

        then: "状态码200，调用成功"
        entity.statusCode.is2xxSuccessful()
    }

    def  "SwaggerResources"() {
        when: "发送一个get请求"
        def entity = restTemplate.getForEntity("/swagger-resources", String)

        then: "状态码200，调用成功"
        entity.statusCode.is2xxSuccessful()
    }
}
