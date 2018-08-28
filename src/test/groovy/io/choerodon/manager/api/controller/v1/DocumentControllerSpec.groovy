package io.choerodon.manager.api.controller.v1

import io.choerodon.manager.IntegrationTestConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author Eugen
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class DocumentControllerSpec extends Specification {
    @Autowired
    private TestRestTemplate restTemplate

    def "Get"() {
//        when: '对【获取服务id对应的版本swagger json字符串】接口发送请求'
//        def entity = restTemplate.getForEntity('/docs/' + 'iam' + '?version=' + null, String)
//        then: '查看接口返回值'
//        entity.statusCode.is2xxSuccessful()
    }

    def "Refresh"() {
    }
}
