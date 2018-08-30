package io.choerodon.manager.api.controller.v1

import io.choerodon.manager.IntegrationTestConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
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
    @Shared
    Map<String, String> info
    @Shared
    String serviceName
    @Shared
    String version

    void setupSpec() {
        info = new HashMap<String, String>()
        info.put("version", "")
        serviceName = 'test_notExist'
        version = ''
    }

    def "Get"() {
        when: '向【获取服务id对应的版本swagger json字符串】接口发送请求'
        def entity = restTemplate.getForEntity('/docs/' + serviceName, String, info)
        then: '结果分析'
        entity.statusCode.is4xxClientError()
    }

    def "Refresh"() {
        when: '向【手动刷新表中swagger json和权限】接口发送请求'
        HttpEntity<Object> httpEntity = new HttpEntity<>()
        def entity = restTemplate.exchange('/docs/permission/refresh/' + serviceName + '?version={version}', HttpMethod.PUT, httpEntity, String, version)
        then: '结果分析'
        entity.statusCode.is5xxServerError()
    }
}
