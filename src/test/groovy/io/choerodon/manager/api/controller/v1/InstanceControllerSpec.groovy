package io.choerodon.manager.api.controller.v1

import io.choerodon.core.domain.Page
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.api.dto.InstanceDetailDTO
import io.choerodon.manager.app.service.InstanceService
import io.choerodon.mybatis.pagehelper.domain.PageRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
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

    @Autowired
    private InstanceController instanceController
    private InstanceService mockInstanceService = Mock(InstanceService)

    void setup() {
        instanceController.setInstanceService(mockInstanceService)
    }

    def "List"() {
        given: '准备参数'
        def service = "test"
        def instanceId = "test"
        def version = "test"
        def status = "test"
        def params = "test"

        def info = new HashMap<String, String>()
        info.put("service", service)
        info.put("instanceId", instanceId)
        info.put("version", version)
        info.put("status", status)
        info.put("params", params)

        when: '向【查询实例列表】发送GET请求'
        def entity = restTemplate.getForEntity('/v1/instances?', Page, info)

        then: '验证状态码成功；验证参数生效'
        entity.statusCode.is2xxSuccessful()
        1 * mockInstanceService.listByOptions(_, _, _ as PageRequest)
    }

    def "Query"() {
        given: '准备参数'
        def instanceId = "1L"

        when: '向【查询实例详情】发送GET请求'
        def entity = restTemplate.getForEntity('/v1/instances/{instance_id}', InstanceDetailDTO, instanceId)

        then: '验证状态码成功；验证参数生效'
        entity.statusCode.is2xxSuccessful()
        1 * mockInstanceService.query(instanceId)
    }

    def "Update"() {
        given: '准备参数'
        def instanceId = "1L"
        def configId = 1L

        when: '向【设置配置为默认配置】接口发送请求'
        HttpEntity<Object> httpEntity = new HttpEntity<>()
        def entity = restTemplate.exchange('/v1/instances/{instance_id}/configs/{config_id}', HttpMethod.PUT, httpEntity, String, instanceId, configId)

        then: '验证状态码成功；验证参数生效'
        entity.statusCode.is2xxSuccessful()
        1 * mockInstanceService.update(instanceId, configId)
    }
}
