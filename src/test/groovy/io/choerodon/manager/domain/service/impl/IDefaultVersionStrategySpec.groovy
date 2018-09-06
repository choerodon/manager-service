package io.choerodon.manager.domain.service.impl

import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.domain.service.VersionStrategy
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
class IDefaultVersionStrategySpec extends Specification {
    @Autowired
    VersionStrategy versionStrategy

    void setup() {
        versionStrategy = new IDefaultVersionStrategy()
    }

    def "CompareVersion"() {
        when: '方法调用'
        def result = versionStrategy.compareVersion(version1, version2)
        then: '结果判断'
        result == correctResult
        where: '结果比对'
        version1      | version2      || correctResult
        "version"     | "version"     || 0
        ""            | "version"     || -1
        "version"     | ""            || 1
        "testversion" | "version"     || 1
        "version"     | "testversion" || 1

    }
}
