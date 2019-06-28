package io.choerodon.manager.app.service.impl

import io.choerodon.base.domain.PageRequest
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.app.service.ServiceService
import io.choerodon.manager.domain.repository.ServiceRepository
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

    private ServiceRepository mockServiceRepository = Mock(ServiceRepository)

    private ServiceService serviceService

    def setup() {
        serviceService = new ServiceServiceImpl(mockServiceRepository, discoveryClient)
    }

    def "List"() {
        given: "构造参数"
        def param = "manager"

        when: "调用list方法 param为空"
        serviceService.list("")

        then: "校验调用次数"
        1 * mockServiceRepository.getAllService()

        when: "调用list方法 param不为空"
        serviceService.list(param)

        then: "校验调用次数"
        1 * mockServiceRepository.selectServicesByFilter(param)
    }

    def "PageManager"() {
        given: "构造参数"
        def serviceName = "manager-service"
        def params = "manager"

        when: "调用接口"
        PageRequest pageRequest = new PageRequest(1, 0)
        def list = serviceService.pageManager(serviceName, params, pageRequest)

        then: "校验调用次数和返回List不为空"
        //1 * discoveryClient.getServices()
        //1 * discoveryClient.getInstances(serviceName)
        0 * _
        !list.getList().isEmpty()
    }
}
