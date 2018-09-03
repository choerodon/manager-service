package io.choerodon.manager.api.controller.v1

import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.api.dto.*
import io.choerodon.manager.app.service.ConfigService
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
class ConfigControllerSpec extends Specification {
    @Autowired
    private TestRestTemplate restTemplate
    @Autowired
    private ConfigController configController
    private ConfigService mockConfigService = Mock(ConfigService)

    void setup() {
        configController.setConfigService(mockConfigService)
    }

    def "create"() {
        given: '创建【创建配置】的参数'
        def validDto = new CreateConfigDTO()
        validDto.setName("test1")
        validDto.setServiceName("test_service")
        validDto.setVersion("v1")
        validDto.setYaml("test: test")

        def inValidDto = new CreateConfigDTO()
        inValidDto.setName("")
        inValidDto.setServiceName("")
        inValidDto.setVersion("")
        inValidDto.setYaml(null)

        when: "用合法的DTO向【创建配置】接口发送POST请求"
        def vaildEntity = restTemplate.postForEntity('/v1/configs', validDto, ConfigDTO)

        then: "验证状态码成功；验证参数生效"
        vaildEntity.statusCode.is2xxSuccessful()
        1 * mockConfigService.create(_)

        when: "用不合法的DTO向【创建配置】接口发送POST请求"
        def inVaildEntity = restTemplate.postForEntity('/v1/configs', inValidDto, ConfigDTO)

        then: "验证状态码成功；验证参数未生效"
        inVaildEntity.statusCode.is2xxSuccessful()
        0 * mockConfigService.create(_)
    }


    def "query"() {
        given: '准备查询参数'
        def configId = 1L
        def type = 'yaml'

        when: '向【查询配置】接口发送GET请求'
        def entity = restTemplate.getForEntity('/v1/configs/{config_id}?type={type}', ConfigDTO, configId, type)

        then: '验证状态码成功；验证参数生效'
        entity.statusCode.is2xxSuccessful()
        1 * mockConfigService.query(configId, type)
    }

    def "queryYaml"() {
        given: '准备查询参数'
        def configId = 1L

        when: '向【查询配置的yaml形式】接口发送GET请求'
        def entity = restTemplate.getForEntity('/v1/configs/{config_id}/yaml', YamlDTO, configId)

        then: '验证状态码成功；验证参数生效'
        entity.statusCode.is2xxSuccessful()
        1 * mockConfigService.queryYaml(configId)
    }

    def "updateConfigDefault"() {
        given: '准备要设为默认配置的ConfigId'
        def configId = 1L

        when: '向【设置配置为默认配置】接口发送PUT请求'
        HttpEntity<ConfigDTO> httpEntity = new HttpEntity<>()
        def entity = restTemplate.exchange('/v1/configs/{config_id}/default', HttpMethod.PUT, httpEntity, ConfigDTO, configId)

        then: '验证状态码成功；验证参数生效'
        entity.statusCode.is2xxSuccessful()
        1 * mockConfigService.setServiceConfigDefault(configId)
    }


    def "addItem"() {
        given: '准备要添加配置项的ConfigId'
        def configId = 1L

        and: '准备要添加的配置项'
        def item = new ItemDto()
        item.setProperty("item")
        item.setValue("value")

        when: '向【增加或修改配置项】接口发POST请求'
        def entity = restTemplate.postForEntity('/v1/configs/{config_id}/items', item, ItemDto, configId)

        then: '验证状态码成功；验证参数生效'
        entity.statusCode.is2xxSuccessful()
        1 * mockConfigService.saveItem(configId, _)
    }

    def "deleteItem"() {
        given: '准备参数'
        def configId = 1L
        def property = 'item'

        when: '向【删除配置项】接口发送DELETE请求'
        HttpEntity<Object> httpEntity = new HttpEntity<>()
        def entity = restTemplate.exchange('/v1/configs/{config_id}/items?property={property}', HttpMethod.DELETE, httpEntity, String, configId, property)

        then: '验证状态码成功；验证参数生效'
        entity.statusCode.is2xxSuccessful()
        1 * mockConfigService.deleteItem(configId, property)
    }

    def "updateConfig"() {
        given: '创建更新所需的参数'
        def configId = 1L

        def type = 'yaml'

        def configDTO = new ConfigDTO()
        configDTO.setName('test')
        Map<String, String> map = new HashMap<>()
        map.put("testadd", "testadd")
        configDTO.setValue(map)
        configDTO.setObjectVersionNumber(1L)

        when: '向【修改配置】接口发送PUT请求'
        HttpEntity<ConfigDTO> httpEntity = new HttpEntity<>(configDTO)
        def entity = restTemplate.exchange('/v1/configs/{config_id}?type={type}', HttpMethod.PUT, httpEntity, ConfigDTO, configId, type)

        then: '验证状态码成功；验证参数生效'
        entity.statusCode.is2xxSuccessful()
        1 * mockConfigService.updateConfig(configId, _ as ConfigDTO, type)
    }

    def "check"() {
        given: '创建校验所需的参数'
        def configCheckDTO = new ConfigCheckDTO()
        configCheckDTO.setName('test')
        configCheckDTO.setConfigVersion('v1')
        configCheckDTO.setServiceName('test')

        when: '向【配置校验】接口发送POST请求'
        def entity = restTemplate.postForEntity('/v1/configs/check', configCheckDTO, String)

        then: '验证状态码成功'
        entity.statusCode.is2xxSuccessful()
        1 * mockConfigService.check(_ as ConfigCheckDTO)
    }

    def "delete"() {
        given: '创建删除所需的参数'
        def configId = 1L

        when: '向【删除配置，默认配置不可删除】接口发送DELETE请求'
        HttpEntity<Object> httpEntity = new HttpEntity<>()
        def entity = restTemplate.exchange('/v1/configs/{config_id}', HttpMethod.DELETE, httpEntity, Boolean, configId)

        then: '验证状态码成功'
        entity.statusCode.is2xxSuccessful()
        1 * mockConfigService.delete(configId)
    }
}
