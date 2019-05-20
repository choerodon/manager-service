package io.choerodon.manager.app.service.impl

import io.choerodon.core.exception.CommonException
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.api.dto.ConfigCheckDTO
import io.choerodon.manager.api.dto.ConfigDTO
import io.choerodon.manager.api.dto.CreateConfigDTO
import io.choerodon.manager.api.dto.ItemDto
import io.choerodon.manager.app.service.ConfigService
import io.choerodon.manager.domain.manager.entity.RouteE
import io.choerodon.manager.domain.manager.entity.ServiceE
import io.choerodon.manager.domain.repository.ConfigRepository
import io.choerodon.manager.domain.repository.RouteRepository
import io.choerodon.manager.domain.repository.ServiceRepository
import io.choerodon.manager.infra.common.utils.config.ConfigUtil
import io.choerodon.manager.infra.dataobject.ConfigDO
import io.choerodon.manager.infra.dataobject.ServiceDO
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
class ConfigServiceImplSpec extends Specification {
    @Autowired
    ConfigService configService
    private ConfigRepository mockConfigRepository = Mock(ConfigRepository)

    private ServiceRepository mockServiceRepository = Mock(ServiceRepository)

    private RouteRepository mockRouteRepository = Mock(RouteRepository)

    private String[] getRouteServices

    def setup() {
        configService = new ConfigServiceImpl(mockConfigRepository, mockServiceRepository, mockRouteRepository)
        getRouteServices = new String[1]
        getRouteServices[0] = "api-gateway"
        configService.setGetRouteServices(getRouteServices)
    }

    def "create"() {
        given: '创建参数'
        def serviceName = "test_service"
        def wrongYaml = "test-test"
        def yaml = "test: test"

        def createConfigDTO_YamlWrong = new CreateConfigDTO()
        createConfigDTO_YamlWrong.setName("test1")
        createConfigDTO_YamlWrong.setServiceName(serviceName)
        createConfigDTO_YamlWrong.setVersion("v1")
        createConfigDTO_YamlWrong.setYaml(wrongYaml)

        def createConfigDTO = new CreateConfigDTO()
        createConfigDTO.setName("test1")
        createConfigDTO.setServiceName(serviceName)
        createConfigDTO.setVersion("v1")
        createConfigDTO.setYaml(yaml)

        def serviceDO = new ServiceDO(name: serviceName)

        and: 'mock serviceRepository.addService'
        mockServiceRepository.getService(_) >> { return serviceDO }

        when: '为服务创建配置 - yaml文件错误 '
        configService.create(createConfigDTO_YamlWrong)

        then: '校验'
        def error = thrown(CommonException)
        error.message == "error.config.yml"

        when: '为服务创建配置 '
        configService.create(createConfigDTO)

        then: '校验'
        noExceptionThrown()
    }

    def "create[ServiceNotExist]"() {
        given: '创建参数'
        def serviceName = "test_service"
        def yaml = "test: test"

        def createConfigDTO = new CreateConfigDTO()
        createConfigDTO.setName("test1")
        createConfigDTO.setServiceName(serviceName)
        createConfigDTO.setVersion("v1")
        createConfigDTO.setYaml(yaml)

        when: '为服务创建配置 - 服务不存在 '
        configService.create(createConfigDTO)

        then: '校验'
        def serviceNotExist = thrown(CommonException)
        serviceNotExist.message == 'error.config.serviceName.notExist'
    }

    def "queryByServiceNameAndConfigVersion"() {
        given: '创建参数'
        def serviceName = "api-gateway"
        def configVersion = "test_version"

        def name = "test"
        def isDefault = false
        def source = "test"

        def configDTO = new ConfigDTO(name, configVersion, isDefault, source)
        def value = new HashMap<String, Object>()
        value.put("test", "test")
        configDTO.setValue(value)

        def routeE = new RouteE()

        routeE.setPath("/test/**")
        routeE.setServiceId("test-service")
        routeE.setUrl("testUrl")
        routeE.setStripPrefix(false)
        routeE.setRetryable(false)
        routeE.setHelperService("testHelperService")
        routeE.setCustomSensitiveHeaders(true)
        routeE.setSensitiveHeaders("testSensitiveHeaders")
        routeE.setName("test")
        def routeList = new ArrayList<RouteE>()
        routeList.add(routeE)

        and: 'mock ConfigRepository.queryByServiceNameAndConfigVersion  mockRouteRepository.getAllRoute'
        mockConfigRepository.queryByServiceNameAndConfigVersion(*_) >> { return configDTO }
        mockRouteRepository.getAllRoute() >> { return routeList }

        when: '根据服务名与版本号查询配置 '
        configService.queryByServiceNameAndConfigVersion(serviceName, configVersion)

        then: '分析配置是否正确'
        noExceptionThrown()
    }


    def "queryByServiceNameAndConfigVersion[Exception]"() {
        given: '创建参数'
        def serviceName = "api-gateway"
        def configVersion = ""

        and: 'mock ConfigRepository.queryByServiceNameAndConfigVersion'
        mockConfigRepository.queryByServiceNameAndConfigVersion(_, _) >> { return null }

        when: '根据服务名与版本号查询配置 '
        configService.queryByServiceNameAndConfigVersion(serviceName, configVersion)

        then: '分析配置是否正确'
        def error = thrown(CommonException)
        error.message == "error.serviceConfigDO.query.serviceNameOrConfigVersionNotFound"
    }

    def "query"() {
        given: '创建参数'
        def configId = 1L
        def type = 'properties'

        def configDO = new ConfigDO()
        configDO.setServiceId(1L)

        def serviceE = new ServiceE()
        serviceE.setName("test_service")

        and: 'mock configRepository.query &  serviceRepository.getService'
        mockConfigRepository.query(configId) >> { return configDO }
        mockServiceRepository.getService(_) >> { return serviceE }

        when: '根据配置Id，type查询配置'
        configService.query(configId, type)

        then: '分析查询所得的配置信息'
        noExceptionThrown()
    }

    def "query[Exception-configNotExist]"() {
        given: '创建参数'
        def configId = 1L
        def type = 'properties'

        def configDO = new ConfigDO()
        configDO.setServiceId(1L)

        def serviceE = new ServiceE()
        serviceE.setName("test_service")

        when: '根据配置Id，type查询配置——异常测试'
        configService.query(configId, type)
        then: '分析查询所得的配置信息'
        def error = thrown(CommonException)
        error.message == "error.config.not.exist"
    }

    def "query[Exception-serviceNotExist]"() {
        given: '创建参数'
        def configId = 1L
        def type = 'properties'

        def configDO = new ConfigDO()
        configDO.setServiceId(1L)

        def serviceE = new ServiceE()
        serviceE.setName("test_service")

        and: 'mock configRepository.query'
        mockConfigRepository.query(configId) >> { return configDO }

        when: '根据配置Id，type查询配置——异常测试'
        configService.query(configId, type)
        then: '分析查询所得的配置信息'
        def error = thrown(CommonException)
        error.message == "error.service.notExist"
    }

    def "queryYaml"() {
        given: '创建参数'
        def configId = 1L
        def configDO = new ConfigDO()
        configDO.setValue('{"item1":"item1","item2":"item2"}')
        configDO.setObjectVersionNumber(1L)
        def serviceE = new ServiceE()
        serviceE.setName("test_service")
        serviceE.setId(1L)

        when: '根据配置Id，查询yaml-配置不存在'
        configService.queryYaml(configId)
        then: '分析异常'
        def configNotExist = thrown(CommonException)
        configNotExist.message == "error.config.not.exist"

        when: '根据配置Id，查询yaml-服务不存在'
        mockConfigRepository.query(configId) >> { return configDO }

        configService.queryYaml(configId)
        then: '分析异常'
        def serviceNotExist = thrown(CommonException)
        serviceNotExist.message == "error.config.service.not.exist"

        when: '根据配置Id，查询yaml'
        mockConfigRepository.query(configId) >> { return configDO }
        mockServiceRepository.getService(_) >> { return serviceE }
        configService.queryYaml(configId)
        then: '结果分析'
        noExceptionThrown()

        when: '根据配置Id，查询yaml-IOException'
        configDO.setValue("testWrong")
        mockConfigRepository.query(configId) >> { return configDO }
        configService.queryYaml(configId)
        then: '结果分析'
        def IOe = thrown(CommonException)
        IOe.message == "error.config.parser"
    }

    def "listByServiceName"() {
        given:
        def serviceName = "test_service"
        def queryInfo = new ConfigDTO()
        def queryParam = ""


        when: '列出指定服务下配置'
        configService.listByServiceName(serviceName, 1,20, queryInfo, queryParam)

        then: '配置列表不为空'
        noExceptionThrown()
    }

    def "setServiceConfigDefault"() {
        given: '指定需要设置默认的配置Id'
        def configId = 1L
        def configDO = new ConfigDO()
        configDO.setName("test")
        configDO.setIsDefault(true)

        and: 'mock configRepository.setConfigDefault'
        mockConfigRepository.setConfigDefault(configId) >> { return configDO }

        when: '根据配置Id将配置设为默认'
        def configDTO = configService.setServiceConfigDefault(configId)

        then: '解析配置是否是默认'
        noExceptionThrown()
        configDTO.isDefault
    }

    def "queryDefaultByServiceName"() {
        given: '创建参数'
        def serviceName = "api-gateway"
        def configDTO = new ConfigDTO()
        def value = new HashMap<String, Object>()
        value.put("test", "test")
        configDTO.setValue(value)
        configDTO.setName("test")

        def routeE = new RouteE()

        routeE.setPath("/test/**")
        routeE.setServiceId("test-service")
        routeE.setUrl("testUrl")
        routeE.setStripPrefix(false)
        routeE.setRetryable(false)
        routeE.setHelperService("testHelperService")
        routeE.setCustomSensitiveHeaders(false)
        routeE.setSensitiveHeaders("testSensitiveHeaders")
        routeE.setName("test")
        def routeList = new ArrayList<RouteE>()
        routeList.add(routeE)

        when: '指定查询所需的服务名-服务名不存在'
        configService.queryDefaultByServiceName(serviceName)
        then: '异常分析'
        def error = thrown(CommonException)
        error.message == 'error.serviceConfigDO.query.serviceNameNotFound'

        when: '指定查询所需的服务名'
        mockConfigRepository.queryDefaultByServiceName(_) >> { return configDTO }
        mockRouteRepository.getAllRoute() >> { return routeList }
        configService.queryDefaultByServiceName(serviceName)
        then: '没有异常'
        noExceptionThrown()
    }


    def "update"() {
        given: '准备参数'
        def configId = 1L
        def configDTO = new ConfigDTO()
        def configDO = new ConfigDO()

        and: 'mock configRepository.update'
        mockConfigRepository.update(_, _) >> { return configDO }

        when: '更新配置'
        configService.update(configId, configDTO)

        then: '解析更新后的配置'
        noExceptionThrown()
    }

    def "updateConfig"() {
        given: '准备需更新的配置'
        def configId = 1L
        def typeYaml = 'yaml'
        def typeProp='properties'
        def configDTO = new ConfigDTO()
        def value = new LinkedHashMap<String, Object>()
        def map = new HashMap<String, Object>()
        map.put("testmap", "testMap")
        value.put("test", map)
        configDTO.setName('update2')
        configDTO.setTxt(ConfigUtil.convertMapToText(value, typeYaml))

        when: '更新配置'
        configService.updateConfig(configId, configDTO, typeYaml)

        then: '解析更新后的配置'
        noExceptionThrown()

        when: '更新配置'
        configDTO.setTxt(ConfigUtil.convertMapToText(value,typeProp))
        configService.updateConfig(configId, configDTO, typeProp)

        then: '解析更新后的配置'
        noExceptionThrown()

        when: '更新配置'
        configDTO.setTxt("test")
        configService.updateConfig(configId, configDTO, typeYaml)

        then: '解析更新后的配置'
        def e = thrown(CommonException)
        e.message == "error.config.txt"

    }

    def "check"() {
        given: '创建参数'
        def configCheckDTO = new ConfigCheckDTO()
        configCheckDTO.setConfigVersion("test_version")
        configCheckDTO.setName("test")
        def serviceDO = new ServiceDO()
        serviceDO.setId(1L)
        def configDO = new ConfigDO()
        configDO.setId(1L)
        when: '配置校验-configDTO为null'
        configService.check(null)
        then: '校验'
        noExceptionThrown()

        when: '配置校验-serviceName为空'
        configService.check(configCheckDTO)
        then: '校验'
        def nullServiceName = thrown(CommonException)
        nullServiceName.message == 'error.config.serviceName.notExist'

        when: '配置校验-service为空'
        configCheckDTO.setServiceName("test_service")
        configService.check(configCheckDTO)
        then: '校验'
        def nullService = thrown(CommonException)
        nullService.message == 'error.config.serviceName.notExist'

        when: '配置校验-configVersion重复'
        configCheckDTO.setServiceName("test_service")
        mockServiceRepository.getService(_) >> { return serviceDO }
        mockConfigRepository.queryByServiceIdAndVersion(_, _) >> { return configDO }
        configService.check(configCheckDTO)
        then: '校验'
        def versionDuplicate = thrown(CommonException)
        versionDuplicate.message == "error.config.insert.versionDuplicate"
    }

    def "check[nameDuplicate]"() {
        given: '创建参数'
        def configCheckDTO = new ConfigCheckDTO()
        configCheckDTO.setConfigVersion("test_version")
        configCheckDTO.setName("test")
        configCheckDTO.setServiceName("test_service")
        def serviceDO = new ServiceDO()
        serviceDO.setId(1L)
        def configDO = new ConfigDO()
        configDO.setId(1L)

        and: 'mock'
        mockServiceRepository.getService(_) >> { return serviceDO }
        mockConfigRepository.queryByServiceIdAndVersion(_, _) >> { return null }
        mockConfigRepository.queryByServiceIdAndName(_, _) >> { return configDO }

        when: '配置校验-name重复'
        configService.check(configCheckDTO)
        then: '校验'
        def nameDuplicate = thrown(CommonException)
        nameDuplicate.message == "error.config.insert.nameDuplicate"

        when: '配置校验'
        configCheckDTO.setName(null)
        configService.check(configCheckDTO)
        then: '校验'
        noExceptionThrown()

    }


    def "saveItem"() {
        given: '创建参数'
        def configId = 1L

        def itemDTO = new ItemDto()
        itemDTO.setProperty('test')
        itemDTO.setValue("test")

        def value = new HashMap<String, Object>()
        value.put("testAddItem", "testAddItem")

        def configDTO = new ConfigDTO()
        configDTO.setValue(value)

        def configDO = new ConfigDO()
        configDO.setServiceId(1L)

        def serviceE = new ServiceE()
        serviceE.setName("test_service")

        when: '增加配置项'
        configService.saveItem(configId, null)
        then: '校验增加配置项——异常'
        def errorADD = thrown(CommonException)
        errorADD.message == "error.config.item.add"

        when: '增加配置项'
        mockConfigRepository.query(configId) >> { return configDO }
        mockServiceRepository.getService(_) >> { return serviceE }
        configService.saveItem(configId, itemDTO)
        then: '校验增加配置项——异常'
        def errorADD2 = thrown(CommonException)
        errorADD2.message == "error.config.item.add"

        when: '增加配置项'
        mockConfigRepository.query(configId) >> { return configDO }
        mockServiceRepository.getService(_) >> { return serviceE }
        mockConfigRepository.update(_, _) >> { return configDO }
        configService.saveItem(configId, itemDTO)
        then: '校验增加配置项'
        noExceptionThrown()
    }

    def "deleteItem"() {
        given: '创建参数'
        def configId = 1L
        def property = "test"

        def configDO = new ConfigDO()
        configDO.setServiceId(1L)
        configDO.setValue('{"test":"test"}')

        def serviceE = new ServiceE()
        serviceE.setName("test_service")

        when: '删除配置项'
        mockConfigRepository.query(configId) >> { return configDO }
        mockServiceRepository.getService(_) >> { return serviceE }
        configService.deleteItem(configId, property)
        then: '结果校验'
        noExceptionThrown()
    }

    def "deleteItem[Exception]"() {
        given: '创建参数'
        def configId = 1L
        def property = "test"

        def configDO = new ConfigDO()
        configDO.setServiceId(1L)

        def serviceE = new ServiceE()
        serviceE.setName("test_service")

        when: '删除配置项-配置为空'
        configService.deleteItem(configId, "")
        then: '结果校验'
        def error = thrown(CommonException)
        error.message == "error.config.item.update"

        when: '删除配置项-item不存在'
        mockConfigRepository.query(configId) >> { return configDO }
        mockServiceRepository.getService(_) >> { return serviceE }
        configService.deleteItem(configId, property)
        then: '结果校验'
        def itemNotExist = thrown(CommonException)
        itemNotExist.message == "error.config.item.not.exist"
    }

    def "delete"() {
        given: '指定要删除的Config'
        def configId = 1L
        when: '根据ConfigId删除配置'
        configService.delete(configId)
        then: '解析删除结果'
        noExceptionThrown()
        1 * mockConfigRepository.delete(configId)
    }
}
