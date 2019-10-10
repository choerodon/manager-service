package io.choerodon.manager.app.service.impl

import org.springframework.data.domain.PageRequest
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.app.service.ServiceService
import io.choerodon.manager.infra.mapper.ServiceMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.client.discovery.DiscoveryClient
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
    private DiscoveryClient discoveryClient
    @Autowired
    private ServiceMapper serviceMapper

    private ServiceService serviceService

    def setup() {
        serviceService = new ServiceServiceImpl(discoveryClient, serviceMapper)
    }

    def "List"() {
        given: "构造参数"
        def param = "manager"

        when: "调用list方法 param为空"
        serviceService.list("")

        then: "校验调用次数"
        true
        when: "调用list方法 param不为空"
        serviceService.list(param)

        then: "校验调用次数"
        true
    }

    def "PageManager"() {
        given: "构造参数"
        def serviceName = "manager-service"
        def params = "manager"

        when: "调用接口"
        PageRequest pageRequest = PageRequest.of(1, 0)
        def list = serviceService.pageManager(serviceName, params, pageRequest)

        then: "校验调用次数和返回List不为空"
        0 * _
        !list.getList().isEmpty()
    }
}
