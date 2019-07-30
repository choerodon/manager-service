package io.choerodon.manager.api.controller.v1

import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.api.dto.ConfigVO
import io.choerodon.manager.app.service.ConfigService
import io.choerodon.manager.app.service.ServiceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ServiceControllerSpec extends Specification {

    private ServiceService mockServiceService = Mock(ServiceService)
    private ConfigService mockConfigService = Mock(ConfigService)

    @Autowired
    private TestRestTemplate restTemplate

    @Autowired
    private ServiceController serviceController

    def setup() {
        serviceController.setServiceService(mockServiceService)
        serviceController.setConfigService(mockConfigService)
    }

    def "PageManager"() {
        given: "构造请求参数"
        def params = "params"
        def serviceName = "manager-service"
        def map = ["service_name": serviceName, "params": params]

        when: "调用分页查询服务列表接口"
        def entity = restTemplate.getForEntity("/v1/services/manager?service_name={service_name}&params={params}", String, map)

        then: "校验状态码和调用次数"
        entity.statusCode.is2xxSuccessful()
        1 * mockServiceService.pageManager(serviceName, params, _)
        0 * _
    }

    def "PageAll"() {
        given: "构造请求参数"
        def params = "params"

        when: "调用查询服务列表接口"
        def entity = restTemplate.getForEntity("/v1/services?param={params}", String, params)

        then: "校验状态码和调用次数"
        entity.statusCode.is2xxSuccessful()
        1 * mockServiceService.list(params)
        0 * _
    }

    def "QueryDefaultConfigByServiceName"() {
        given: "构造请求参数"
        def serviceName = "manager-service"

        when: "调用通过服务名获取配置信息接口"
        def entity = restTemplate.getForEntity("/v1/services/{service_name}/configs/default", String, serviceName)

        then: "校验状态码和调用次数"
        entity.statusCode.is2xxSuccessful()
        1 * mockConfigService.queryDefaultByServiceName(serviceName)
        0 * _
    }

    def "QueryConfigByServiceNameAndVersion"() {
        given: "构造请求参数"
        def serviceName = "manager-service"
        def configVersion = "0.10.0"

        when: "调用通过服务名和配置版本获取配置信息接口"
        def entity = restTemplate.getForEntity("/v1/services/{service_name}/configs/{config_version:.*}", String, serviceName, configVersion)

        then: "校验状态码和调用次数"
        entity.statusCode.is2xxSuccessful()
        1 * mockConfigService.queryByServiceNameAndConfigVersion(serviceName, configVersion)
        0 * _
    }

    def "List"() {
        given: "构造pageRequest和请求参数"
        def serviceName = "manager-service"
        def params = "params"
        def map = ["service-name": serviceName, "params": params, "name": "manager", "configVersion": "0.10.0", "isDefault": false, "source": "source"]

        when: "调用分页查询服务的配置信息接口"
        def entity = restTemplate.getForEntity("/v1/services/{service-name}/configs?params={params}&name={name}" +
                "&configVersion={configVersion}&isDefault={isDefault}&source={source}", String, map)

        then: "校验状态码和调用次数"
        entity.statusCode.is2xxSuccessful()
//        1 * mockConfigService.listByServiceName(serviceName, _, _ as ConfigVO, params)
    }
}
