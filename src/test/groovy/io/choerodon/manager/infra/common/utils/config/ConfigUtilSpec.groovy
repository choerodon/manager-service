package io.choerodon.manager.infra.common.utils.config

import io.choerodon.manager.IntegrationTestConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author Eugen
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ConfigUtilSpec extends Specification {
    def "ConvertTextToMap"() {
        given: '参数准备'
        def innerMap = new LinkedHashMap<String, Object>()
        innerMap.put("innerMap", "innerMap")
        def innerList = new ArrayList()
        innerList.add("innerList")
        def map = new HashMap<String, Object>()
        map.put("text", "text")
        map.put("innerMap", innerMap)
        map.put("innerList", innerList)

        def text = ConfigUtil.convertMapToText(map, 'yaml')

        when: '方法调用'
        ConfigUtil.convertTextToMap('yaml', text)
        then: '结果验证'
        noExceptionThrown()

        when: '方法调用'
        ConfigUtil.convertTextToMap('', text)
        then: '结果验证'
        noExceptionThrown()
    }

    def "ConvertJsonToYaml"() {
        given: '参数准备'
        def text = '{"test":"test"}'
        when: '方法调用'
        def result = ConfigUtil.convertJsonToYaml(text)
        then: '结果验证'
        noExceptionThrown()
        result == '---\ntest: "test"\n'
    }
}
