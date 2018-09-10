package io.choerodon.manager.infra.common.utils.config

import io.choerodon.core.exception.CommonException
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
class ConfigFileFormatSpec extends Specification {

    def "FromString"() {
        when: '方法调用'
        def result = ConfigFileFormat.fromString(value)
        then: '结果比对'
        result == type
        where: '方法返回值'
        value        || type
        "properties" || ConfigFileFormat.PROPERTIES
        "yaml"       || ConfigFileFormat.YAML
        "yml"        || ConfigFileFormat.YML
        "json"       || ConfigFileFormat.JSON
    }

    def "FromString[Exception]"() {
        when: '声明枚举类'
        ConfigFileFormat.fromString(value)
        then: '结果比对'
        def ex = thrown(CommonException)
        ex.message == errorMsg
        where: '方法返回值'
        value  || errorMsg
        "prop" || "error.format.type"
        ""     || "error.format.type"
    }

    def "IsValidFormat"() {
        when: '方法调用'
        def result = ConfigFileFormat.isValidFormat("yaml")
        then: '结果比对'
        noExceptionThrown()
        result == true
    }
}
