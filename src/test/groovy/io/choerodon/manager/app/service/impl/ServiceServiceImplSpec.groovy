package io.choerodon.manager.app.service.impl

import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.mybatis.pagehelper.domain.PageRequest
import io.choerodon.mybatis.pagehelper.domain.Sort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ServiceServiceImplSpec extends Specification {

    @Autowired
    private ServiceServiceImpl serviceService

    def "List"() {
        when: "调用list param为空"
        def list = serviceService.list("")

        then: "返回List不为空"
        !list.isEmpty()

        when: "调用list param不为空"
        list = serviceService.list("manager")

        then: "返回List不为空"
        !list.isEmpty()
    }

    def "PageManager"() {
        given: "构造pageRequest"
        def order = new Sort.Order("id")
        def pageRequest = new PageRequest(0, 10, new Sort(order))

        when: "调用"
        def list = serviceService.pageManager("manager-service", "manager", pageRequest)

        then: "返回List不为空"
        !list.isEmpty()
    }
}
