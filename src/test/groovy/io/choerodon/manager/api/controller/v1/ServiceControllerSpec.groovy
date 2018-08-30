package io.choerodon.manager.api.controller.v1

import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.mybatis.pagehelper.domain.PageRequest
import io.choerodon.mybatis.pagehelper.domain.Sort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ServiceControllerSpec extends Specification {

    @Autowired
    private TestRestTemplate restTemplate

    def "PageManager"() {
        given: "构造pageRequest"
        def url = "/v1/services/manager"
        def order = new Sort.Order("id")
        def pageRequest = new PageRequest(0, 20, new Sort(order))

        when: "发送一个带参数get请求"
        def map = new HashMap<String, Object>();
        map.put("service_name", "manager-service");
        map.put("params", "");
        map.put("pageRequest", pageRequest);
        def entity = restTemplate.getForEntity(url, String, map)

        then: "校验状态码"
        noExceptionThrown()
        entity.statusCode.is2xxSuccessful()
    }

    def "PageAll"() {
        given: "构造url"
        def url = "/v1/services"

        when: "发送一个带参数get请求"
        def map = new HashMap<String, String>();
        map.put("params", "");
        def entity = restTemplate.getForEntity(url, String, map)

        then: "校验状态码"
        noExceptionThrown()
        entity.statusCode.is2xxSuccessful()
    }

    def "QueryDefaultConfigByServiceName"() {
        when: "发送一个get请求"
        def entity = restTemplate.getForEntity("/v1/services/{service_name}/configs/default", String, "manager-service")

        then: "校验状态码"
        noExceptionThrown()
        entity.statusCode.is2xxSuccessful()
    }

    def "QueryConfigByServiceNameAndVersion"() {
        when: "发送一个get请求"
        def entity = restTemplate.getForEntity("/v1/services/{service_name}/configs/1", String, "manager-service")

        then: "校验状态码"
        noExceptionThrown()
        entity.statusCode.is2xxSuccessful()
    }

    def "List"() {
        given: "构造url"
        def url = "/v1/services/manager-service/configs"
        and: "构造pageRequest"
        def order = new Sort.Order("id")
        def pageRequest = new PageRequest(0, 20, new Sort(order))

        when: "发送一个带参数get请求"
        def map = new HashMap<String, Object>();
        map.put("params", "");
        map.put("pageRequest", pageRequest);
        map.put("name", "");
        map.put("configVersion", "1");
        map.put("isDefault", "");
        map.put("source", "");
        def entity = restTemplate.getForEntity(url, String, map)

        then: "校验状态码"
        noExceptionThrown()
        entity.statusCode.is2xxSuccessful()
    }
}
