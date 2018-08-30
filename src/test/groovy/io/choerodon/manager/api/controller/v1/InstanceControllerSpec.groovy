package io.choerodon.manager.api.controller.v1

import io.choerodon.core.domain.Page
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.api.dto.InstanceDetailDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author Eugen
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class InstanceControllerSpec extends Specification {
    @Autowired
    private TestRestTemplate restTemplate
    @Shared
    Map<String, String> info
    @Shared
    Long instanceId
    @Shared
    Long configId

    void setupSpec() {
        info = new HashMap<String, String>()
        info.put("service", "")
        info.put("instanceId", "")
        info.put("version", "")
        info.put("status", "")
        info.put("params", "")

        instanceId = 1L
        configId = 1L
    }

    def "List"() {
        when: '向【查询实例列表】发送请求'
        def entity = restTemplate.getForEntity('/v1/instances?', Page, info)
        then: '结果分析'
        entity.statusCode.is2xxSuccessful()
    }

    def "Query"() {
        when: '向【查询实例详情】'
        def entity = restTemplate.getForEntity('/v1/instances/' + instanceId, InstanceDetailDTO)
        then: '结果分析'
        entity.statusCode.is2xxSuccessful()
    }

    def "Update"() {
        when: '向【设置配置为默认配置】接口发送请求'
        HttpEntity<Object> httpEntity = new HttpEntity<>()
        def entity = restTemplate.exchange('/v1/instances/' + instanceId + '/configs/' + configId, HttpMethod.PUT, httpEntity, String)
        then: '结果分析'
        entity.statusCode.is2xxSuccessful()
    }
}
