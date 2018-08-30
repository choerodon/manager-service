package io.choerodon.manager.api.controller.v1

import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.api.dto.ConfigDTO
import io.choerodon.manager.api.dto.CreateConfigDTO
import io.choerodon.manager.api.dto.ItemDto
import io.choerodon.manager.api.dto.YamlDTO
import io.choerodon.manager.domain.manager.entity.ServiceE
import io.choerodon.manager.domain.repository.ServiceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import spock.lang.Shared
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
    private ServiceRepository serviceRepository

    @Shared
    CreateConfigDTO data1
    @Shared
    CreateConfigDTO data2
    @Shared
    ConfigDTO configDTO
    @Shared
    ServiceE serviceE
    @Shared
    Long id1
    @Shared
    Long id2
    @Shared
    ItemDto item1

    void setupSpec() {
        data1 = new CreateConfigDTO()
        data1.setName("test1")
        data1.setServiceName("test_service")
        data1.setVersion("v1")
        data1.setYaml("test: test")

        data2 = new CreateConfigDTO()
        data2.setName("test2")
        data2.setServiceName("test_service")
        data2.setVersion("default")
        data2.setYaml("test: test")

        configDTO = new ConfigDTO()
        configDTO.setName()
        configDTO.setValue()
        Map<String, String> map = new HashMap<>()
        map.put("testadd", "testadd")
        configDTO.setValue(map)
        configDTO.setObjectVersionNumber(2L)

        serviceE = new ServiceE()
        serviceE.setName("test_service")

        item1 = new ItemDto()
        item1.setProperty("item1")
        item1.setValue("value1")
    }

    def "create"() {
        given: '准备创建配置所需的服务'
        serviceE = serviceRepository.addService(serviceE)
        configDTO.setServiceId(serviceE.getId())
        when: '向【创建配置】接口发请求'
        def entity1 = restTemplate.postForEntity('/v1/configs', data1, ConfigDTO)
        def entity2 = restTemplate.postForEntity('/v1/configs', data2, ConfigDTO)
        id1 = entity1.body.id
        id2 = entity2.body.id
        then: '查看接口返回值'
        entity1.statusCode.is2xxSuccessful()
        entity2.statusCode.is2xxSuccessful()


    }


    def "query"() {
        given: '准备要查询的ConfigId'
        def configId = id1
        when: '向【查询配置】接口发送请求'
        def entity = restTemplate.getForEntity('/v1/configs/' + configId, ConfigDTO)
        then: '查询结果'
        entity.statusCode.is2xxSuccessful()
    }

    def "queryYaml"() {
        given: '准备要查询的ConfigId'
        def configId = id1
        when: '向【查询配置的yaml形式】接口发送请求'
        def entity = restTemplate.getForEntity('/v1/configs/' + configId + "/yaml", YamlDTO)
        then: '查询结果'
        entity.statusCode.is2xxSuccessful()
    }

    def "updateConfigDefault"() {
        given: '准备要设为默认配置的ConfigId'
        def configId = id2
        when: '向【设置配置为默认配置】接口发送请求'
        HttpEntity<ConfigDTO> httpEntity = new HttpEntity<>()
        def entity = restTemplate.exchange('/v1/configs/' + configId + '/default', HttpMethod.PUT, httpEntity, ConfigDTO)
        then: ''
        entity.statusCode.is2xxSuccessful()
    }


    def "addItem"() {
        given: '准备要添加配置项的ConfigId'
        def configId = id1
        when: '向【增加或修改配置项】接口发请求'
        def entity = restTemplate.postForEntity('/v1/configs/' + configId + '/items', item1, ItemDto)
        then: '查看接口返回值'
        entity.statusCode.is2xxSuccessful()
    }

    def "deleteItem"() {
        when: '向【删除配置项】接口发送请求'
        HttpEntity<Object> httpEntity = new HttpEntity<>()
        def entity = restTemplate.exchange('/v1/configs/{config_id}/items?property={property}', HttpMethod.DELETE, httpEntity, String, id1, 'item1')

        then: '删除结果'
        entity.statusCode.is2xxSuccessful()
    }

    def "updateConfig"() {
        when: '向【修改配置】接口发送请求'
        HttpEntity<ConfigDTO> httpEntity = new HttpEntity<>(configDTO)
        def entity = restTemplate.exchange('/v1/configs/{config_id}?type={type}', HttpMethod.PUT, httpEntity, ConfigDTO, id1, 'yaml')
        then: '查看修改结果'
        entity.statusCode.is2xxSuccessful()
    }

    def "check"() {
        when: '向【配置校验】接口发送请求'
        def entity = restTemplate.postForEntity('/v1/configs/check', configDTO, String)
        then: '查看校验结果'
        entity.statusCode.is2xxSuccessful()
    }

    def "delete"() {
        when: '向【删除配置，默认配置不可删除】接口发送请求'
        HttpEntity<Object> httpEntity = new HttpEntity<>()
        def entity = restTemplate.exchange('/v1/configs/' + id1, HttpMethod.DELETE, httpEntity, Boolean)

        then: '删除结果'
        entity.statusCode.is2xxSuccessful()
        serviceRepository.deleteService(serviceE.getId())
    }
}
