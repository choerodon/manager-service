package io.choerodon.manager.infra.feign.fallback

import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.infra.feign.ConfigServerClient
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
class ConfigServerClientFallbackSpec extends Specification {
    @Autowired
    private ConfigServerClient configServerClient

    void setup() {
        configServerClient=new ConfigServerClientFallback()
    }

    def "Refresh"() {
        given: '参数准备'
        def instanceId = 'test_server:test_ip:test_port'
        def map = new LinkedHashMap<String, String>()
        map.put("path", "pathTest")
        map.put("instanceId", instanceId)
        map.put("configVersion", "test_version")

        when: '方法调用'
        configServerClient.refresh(map)
        then: '结果分析'
        noExceptionThrown()
    }
}
