//package io.choerodon.manager.app.service.impl
//
//import io.choerodon.base.domain.PageRequest
//import io.choerodon.core.exception.CommonException
//import io.choerodon.manager.IntegrationTestConfiguration
//import io.choerodon.manager.api.dto.ConfigCheckDTO
//import io.choerodon.manager.api.dto.ConfigVO
//import io.choerodon.manager.api.dto.CreateConfigDTO
//import io.choerodon.manager.api.dto.ItemDto
//import io.choerodon.manager.app.service.ConfigService
//import io.choerodon.manager.infra.asserts.ConfigAssertHelper
//import io.choerodon.manager.infra.common.utils.config.ConfigUtil
//import io.choerodon.manager.infra.dto.ConfigDTO
//import io.choerodon.manager.infra.dto.RouteDTO
//import io.choerodon.manager.infra.dto.ServiceDTO
//import io.choerodon.manager.infra.mapper.ConfigMapper
//import io.choerodon.manager.infra.mapper.RouteMapper
//import io.choerodon.manager.infra.mapper.ServiceMapper
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.context.annotation.Import
//import spock.lang.Specification
//
//import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
//
///**
// * @author Eugen
// */
//@SpringBootTest(webEnvironment = RANDOM_PORT)
//@Import(IntegrationTestConfiguration)
//class ConfigServiceImplSpec extends Specification {
//    @Autowired
//    ConfigService configService
//    ConfigMapper configMapper = Mock(ConfigMapper)
//    ServiceMapper serviceMapper = Mock(ServiceMapper)
//    ConfigAssertHelper configAssertHelper = Mock(ConfigAssertHelper)
//    RouteMapper routeMapper = Mock(RouteMapper)
//
//
//    private String[] getRouteServices
//
//    def setup() {
//        configService = new ConfigServiceImpl(configMapper, serviceMapper, configAssertHelper, routeMapper)
//        getRouteServices = new String[1]
//        getRouteServices[0] = "api-gateway"
//        configService.setGetRouteServices(getRouteServices)
//    }
//
//    def "create"() {
//        given: '创建参数'
//        def serviceName = "test_service"
//        def wrongYaml = "test-test"
//        def yaml = "test: test"
//
//        def createConfigDTO_YamlWrong = new CreateConfigDTO()
//        createConfigDTO_YamlWrong.setName("test1")
//        createConfigDTO_YamlWrong.setServiceName(serviceName)
//        createConfigDTO_YamlWrong.setVersion("v1")
//        createConfigDTO_YamlWrong.setYaml(wrongYaml)
//
//        def createConfigDTO = new CreateConfigDTO()
//        createConfigDTO.setName("test1")
//        createConfigDTO.setServiceName(serviceName)
//        createConfigDTO.setVersion("v1")
//        createConfigDTO.setYaml(yaml)
//
//        def serviceDTO = new ServiceDTO(name: serviceName)
//
//        and: 'mock serviceRepository.addService'
//        serviceMapper.selectOne(_) >> { return serviceDTO }
//
//        when: '为服务创建配置 - yaml文件错误 '
//        configService.create(createConfigDTO_YamlWrong)
//
//        then: '校验'
//        def error = thrown(CommonException)
//        error.message == "error.config.yml"
//
//        when: '为服务创建配置 '
//        configService.create(createConfigDTO)
//
//        then: '校验'
//        1*configMapper.insert(_)>>1
//        1*configMapper.selectByPrimaryKey(_)>> Mock(ConfigDTO)
//        noExceptionThrown()
//    }
//
//    def "create[ServiceNotExist]"() {
//        given: '创建参数'
//        def serviceName = "test_service"
//        def yaml = "test: test"
//
//        def createConfigDTO = new CreateConfigDTO()
//        createConfigDTO.setName("test1")
//        createConfigDTO.setServiceName(serviceName)
//        createConfigDTO.setVersion("v1")
//        createConfigDTO.setYaml(yaml)
//
//        when: '为服务创建配置 - 服务不存在 '
//        configService.create(createConfigDTO)
//
//        then: '校验'
//        def serviceNotExist = thrown(CommonException)
//        serviceNotExist.message == 'error.config.serviceName.notExist'
//    }
//
//    def "queryByServiceNameAndConfigVersion"() {
//        given: '创建参数'
//        def serviceName = "api-gateway"
//        def configVersion = "test_version"
//
//        def name = "test"
//        def isDefault = false
//        def source = "test"
//
//        def configDTO = new ConfigDTO(name, configVersion, isDefault, source)
//        def value = new HashMap<String, Object>()
//        value.put("test", "test")
//        configDTO.setValue(value)
//
//        def routeDTO = new RouteDTO()
//
//        routeDTO.setPath("/test/**")
//        routeDTO.setServiceId("test-service")
//        routeDTO.setUrl("testUrl")
//        routeDTO.setStripPrefix(false)
//        routeDTO.setRetryable(false)
//        routeDTO.setHelperService("testHelperService")
//        routeDTO.setCustomSensitiveHeaders(true)
//        routeDTO.setSensitiveHeaders("testSensitiveHeaders")
//        routeDTO.setName("test")
//        def routeList = new ArrayList<RouteDTO>()
//        routeList.add(routeDTO)
//
//        and: 'mock ConfigRepository.queryByServiceNameAndConfigVersion  mockRouteRepository.getAllRoute'
//        List list = new ArrayList()
//        list << configDTO
//        configMapper.selectByServiceAndConfigVersion(_, _) >> { return list }
//        routeMapper.selectAll() >> { return routeList }
//
//        when: '根据服务名与版本号查询配置 '
//        configService.queryByServiceNameAndConfigVersion(serviceName, configVersion)
//
//        then: '分析配置是否正确'
//        noExceptionThrown()
//    }
//
//
//    def "queryByServiceNameAndConfigVersion[Exception]"() {
//        given: '创建参数'
//        def serviceName = "api-gateway"
//        def configVersion = ""
//
//        and: 'mock ConfigRepository.queryByServiceNameAndConfigVersion'
//        configMapper.selectByServiceAndConfigVersion(_, _) >> { return null }
//
//        when: '根据服务名与版本号查询配置 '
//        configService.queryByServiceNameAndConfigVersion(serviceName, configVersion)
//
//        then: '分析配置是否正确'
//        def error = thrown(CommonException)
//        error.message == "error.serviceConfigDO.query.serviceNameOrConfigVersionNotFound"
//    }
//
//    def "query"() {
//        given: '创建参数'
//        def configId = 1L
//        def type = 'properties'
//
//        def configDO = new ConfigDTO()
//        configDO.setServiceId(1L)
//
//        def serviceDTO = new ServiceDTO()
//        serviceDTO.setName("test_service")
//
//        and: 'mock configRepository.query &  serviceRepository.getService'
//        configMapper.selectByPrimaryKey(_) >> { return configDO }
//        configAssertHelper.notExisted(_) >> { return configDO }
//        serviceMapper.selectByPrimaryKey(_) >> { return serviceDTO }
//
//        when: '根据配置Id，type查询配置'
//        configService.query(configId, type)
//
//        then: '分析查询所得的配置信息'
//        noExceptionThrown()
//    }
//
//    def "query[Exception-configNotExist]"() {
//        given: '创建参数'
//        def configId = 1L
//        def type = 'properties'
//
//        def configDO = new ConfigDTO()
//        configDO.setServiceId(1L)
//
//        def serviceDTO = new ServiceDTO()
//        serviceDTO.setName("test_service")
//
//        when: '根据配置Id，type查询配置——异常测试'
//        configService.query(configId, type)
//        then: '分析查询所得的配置信息'
//        def error = thrown(CommonException)
//        error.message == "error.config.not.exist"
//    }
//
//    def "query[Exception-serviceNotExist]"() {
//        given: '创建参数'
//        def configId = 1L
//        def type = 'properties'
//
//        def configDO = new ConfigDTO()
//        configDO.setServiceId(1L)
//
//        def serviceDTO = new ServiceDTO()
//        serviceDTO.setName("test_service")
//
//        and: 'mock configRepository.query'
//        configAssertHelper.notExisted(configId) >> { return configDO }
//
//        when: '根据配置Id，type查询配置——异常测试'
//        configService.query(configId, type)
//        then: '分析查询所得的配置信息'
//        def error = thrown(CommonException)
//        error.message == "error.service.notExist"
//    }
//
//    def "queryYaml"() {
//        given: '创建参数'
//        def configId = 1L
//        def configDO = new ConfigDTO()
//        configDO.setValue('{"item1":"item1","item2":"item2"}')
//        configDO.setObjectVersionNumber(1L)
//        def serviceDTO = new ServiceDTO()
//        serviceDTO.setName("test_service")
//        serviceDTO.setId(1L)
//
//        when: '根据配置Id，查询yaml-配置不存在'
//        configService.queryYaml(configId)
//        then: '分析异常'
//        def configNotExist = thrown(CommonException)
//        configNotExist.message == "error.config.not.exist"
//
//        when: '根据配置Id，查询yaml-服务不存在'
//        configAssertHelper.notExisted(configId) >> { return configDO }
//
//        configService.queryYaml(configId)
//        then: '分析异常'
//        def serviceNotExist = thrown(CommonException)
//        serviceNotExist.message == "error.config.service.not.exist"
//
//        when: '根据配置Id，查询yaml'
//        configAssertHelper.notExisted(configId) >> { return configDO }
//        serviceMapper.selectByPrimaryKey(_) >> { return serviceDTO }
//        configService.queryYaml(configId)
//        then: '结果分析'
//        noExceptionThrown()
//
//        when: '根据配置Id，查询yaml-IOException'
//        configDO.setValue("testWrong")
//        configAssertHelper.notExisted(_) >> { return configDO }
//        configService.queryYaml(configId)
//        then: '结果分析'
//        def IOe = thrown(CommonException)
//        IOe.message == "error.config.parser"
//    }
//
//    def "listByServiceName"() {
//        given:
//        def serviceName = "test_service"
//        def queryInfo = new ConfigDTO()
//        def queryParam = ""
//
//
//        when: '列出指定服务下配置'
//        PageRequest pageRequest = new PageRequest(1, 20)
//        configService.listByServiceName(serviceName, pageRequest, queryInfo, queryParam)
//
//        then: '配置列表不为空'
//        noExceptionThrown()
//    }
//
//    def "setServiceConfigDefault"() {
//        given: '指定需要设置默认的配置Id'
//        def configId = 1L
//        def configDO = new ConfigDTO()
//        configDO.setName("test")
//        configDO.setIsDefault(true)
//
//        and: 'mock configRepository.setConfigDefault'
//        configMapper.selectOne() >> { return configDO }
//        configAssertHelper.notExisted(_) >> { return configDO }
//
//        when: '根据配置Id将配置设为默认'
//        def configDTO = configService.updateConfigDefault(configId)
//
//        then: '解析配置是否是默认'
//        noExceptionThrown()
//        configDTO.isDefault
//    }
//
//    def "queryDefaultByServiceName"() {
//        given: '创建参数'
//        def serviceName = "api-gateway"
//        def configDTO = new ConfigDTO()
//        def value = new HashMap<String, Object>()
//        value.put("test", "test")
//        configDTO.setValue(value)
//        configDTO.setName("test")
//
//        def routeDTO = new RouteDTO()
//
//        routeDTO.setPath("/test/**")
//        routeDTO.setServiceId("test-service")
//        routeDTO.setUrl("testUrl")
//        routeDTO.setStripPrefix(false)
//        routeDTO.setRetryable(false)
//        routeDTO.setHelperService("testHelperService")
//        routeDTO.setCustomSensitiveHeaders(false)
//        routeDTO.setSensitiveHeaders("testSensitiveHeaders")
//        routeDTO.setName("test")
//        def routeList = new ArrayList<RouteDTO>()
//        routeList.add(routeDTO)
//
//        when: '指定查询所需的服务名-服务名不存在'
//        configService.queryDefaultByServiceName(serviceName)
//        then: '异常分析'
//        def error = thrown(CommonException)
//        error.message == 'error.serviceConfigDO.query.serviceNameNotFound'
//
//        when: '指定查询所需的服务名'
//        configMapper.selectByServiceDefault(_)>> { return configDTO }
//        routeMapper.selectAll()>> { return routeList }
//        configService.queryDefaultByServiceName(serviceName)
//        then: '没有异常'
//        noExceptionThrown()
//    }
//
//
//    def "update"() {
//        given: '准备参数'
//        def configId = 1L
//        def configDTO = new ConfigDTO()
////        def configDO = new ConfigDTO()
//
////        and: 'mock configRepository.update'
////
////        configMapper.update(_, _) >> { return configDO }
//
//        when: '更新配置'
//        configService.update(configId, configDTO)
//
//        then: '解析更新后的配置'
//        noExceptionThrown()
//    }
//
//    def "updateConfig"() {
//        given: '准备需更新的配置'
//        def configId = 1L
//        def typeYaml = 'yaml'
//        def typeProp = 'properties'
//        def configVO = new ConfigVO()
//        def value = new LinkedHashMap<String, Object>()
//        def map = new HashMap<String, Object>()
//        map.put("testmap", "testMap")
//        value.put("test", map)
//        configVO.setName('update2')
//        configVO.setTxt(ConfigUtil.convertMapToText(value, typeYaml))
//
//        when: '更新配置'
//        configService.updateConfig(configId, configVO, typeYaml)
//
//        then: '解析更新后的配置'
//        noExceptionThrown()
//
//        when: '更新配置'
//        configVO.setTxt(ConfigUtil.convertMapToText(value, typeProp))
//        configService.updateConfig(configId, configVO, typeProp)
//
//        then: '解析更新后的配置'
//        noExceptionThrown()
//
//        when: '更新配置'
//        configVO.setTxt("test")
//        configService.updateConfig(configId, configVO, typeYaml)
//
//        then: '解析更新后的配置'
//        def e = thrown(CommonException)
//        e.message == "error.config.txt"
//
//    }
//
//    def "check"() {
//        given: '创建参数'
//        def configCheckDTO = new ConfigCheckDTO()
//        configCheckDTO.setConfigVersion("test_version")
//        configCheckDTO.setName("test")
//        def serviceDO = new ServiceDTO()
//        serviceDO.setId(1L)
//        def configDO = new ConfigDTO()
//        configDO.setId(1L)
//        when: '配置校验-configDTO为null'
//        configService.check(null)
//        then: '校验'
//        noExceptionThrown()
//
//        when: '配置校验-serviceName为空'
//        configService.check(configCheckDTO)
//        then: '校验'
//        def nullServiceName = thrown(CommonException)
//        nullServiceName.message == 'error.config.serviceName.notExist'
//
//        when: '配置校验-service为空'
//        configCheckDTO.setServiceName("test_service")
//        configService.check(configCheckDTO)
//        then: '校验'
//        def nullService = thrown(CommonException)
//        nullService.message == 'error.config.serviceName.notExist'
//
//        when: '配置校验-configVersion重复'
//        configCheckDTO.setServiceName("test_service")
//        serviceMapper.selectOne(_)>>{ return serviceDO }
//        configMapper.selectByServiceAndConfigVersion(_,_)>> { return configDO }
//        configService.check(configCheckDTO)
//        then: '校验'
//        def versionDuplicate = thrown(CommonException)
//        versionDuplicate.message == "error.config.insert.versionDuplicate"
//    }
//
//    def "check[nameDuplicate]"() {
//        given: '创建参数'
//        def configCheckDTO = new ConfigCheckDTO()
//        configCheckDTO.setConfigVersion("test_version")
//        configCheckDTO.setName("test")
//        configCheckDTO.setServiceName("test_service")
//        def serviceDO = new ServiceDTO()
//        serviceDO.setId(1L)
//        def configDO = new ConfigDTO()
//        configDO.setId(1L)
//
//        and: 'mock'
//        serviceMapper.selectOne(_)>> { return serviceDO }
//        configMapper.selectByServiceAndConfigVersion(_,_) >> { return null }
////        mockServiceRepository.getService(_) >> { return serviceDO }
////        mockConfigRepository.queryByServiceIdAndVersion(_, _) >> { return null }
////        mockConfigRepository.queryByServiceIdAndName(_, _) >> { return configDO }
//
//        when: '配置校验-name重复'
//        configService.check(configCheckDTO)
//        then: '校验'
//        def nameDuplicate = thrown(CommonException)
//        nameDuplicate.message == "error.config.insert.nameDuplicate"
//
//        when: '配置校验'
//        configCheckDTO.setName(null)
//        configService.check(configCheckDTO)
//        then: '校验'
//        noExceptionThrown()
//
//    }
//
//
//    def "saveItem"() {
//        given: '创建参数'
//        def configId = 1L
//
//        def itemDTO = new ItemDto()
//        itemDTO.setProperty('test')
//        itemDTO.setValue("test")
//
//        def value = new HashMap<String, Object>()
//        value.put("testAddItem", "testAddItem")
//
//        def configDTO = new ConfigDTO()
//        configDTO.setValue(value)
//
//        def configDO = new ConfigDTO()
//        configDO.setServiceId(1L)
//
//        def serviceDTO = new ServiceDTO()
//        serviceDTO.setName("test_service")
//
//        when: '增加配置项'
//        configService.saveItem(configId, null)
//        then: '校验增加配置项——异常'
//        def errorADD = thrown(CommonException)
//        errorADD.message == "error.config.item.add"
//
//        when: '增加配置项'
//        configAssertHelper.notExisted(_)>> { return configDO }
//        serviceMapper.selectByPrimaryKey(_)>>{ return serviceDTO }
//        configService.saveItem(configId, itemDTO)
//        then: '校验增加配置项——异常'
//        def errorADD2 = thrown(CommonException)
//        errorADD2.message == "error.config.item.add"
//
//        when: '增加配置项'
//        configService.saveItem(configId, itemDTO)
//        then: '校验增加配置项'
//        noExceptionThrown()
//    }
//
//    def "deleteItem"() {
//        given: '创建参数'
//        def configId = 1L
//        def property = "test"
//
//        def configDO = new ConfigDTO()
//        configDO.setServiceId(1L)
//        configDO.setValue('{"test":"test"}')
//
//        def serviceDTO = new ServiceDTO()
//        serviceDTO.setName("test_service")
//
//        when: '删除配置项'
//        configAssertHelper.notExisted(_)>>{ return configDO }
//        serviceMapper.selectOne(_)>>{ return serviceDTO }
//        configService.deleteItem(configId, property)
//        then: '结果校验'
//        noExceptionThrown()
//    }
//
//    def "deleteItem[Exception]"() {
//        given: '创建参数'
//        def configId = 1L
//        def property = "test"
//
//        def configDO = new ConfigDTO()
//        configDO.setServiceId(1L)
//
//        def serviceDTO = new ServiceDTO()
//        serviceDTO.setName("test_service")
//
//        when: '删除配置项-配置为空'
//        configService.deleteItem(configId, "")
//        then: '结果校验'
//        def error = thrown(CommonException)
//        error.message == "error.config.item.update"
//
//        when: '删除配置项-item不存在'
//        configAssertHelper.notExisted(_)>>{ return configDO }
//        serviceMapper.selectOne(_)>>{ return serviceDTO }
//        configService.deleteItem(configId, property)
//        then: '结果校验'
//        def itemNotExist = thrown(CommonException)
//        itemNotExist.message == "error.config.item.not.exist"
//    }
//
//    def "delete"() {
//        given: '指定要删除的Config'
//        def configId = 1L
//        when: '根据ConfigId删除配置'
//        configService.delete(configId)
//        then: '解析删除结果'
//        noExceptionThrown()
////        1 * mockConfigRepository.delete(configId)
//    }
//}
