package io.choerodon.manager.app.service.impl

import io.choerodon.core.exception.CommonException
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.api.dto.ConfigCheckDTO
import io.choerodon.manager.api.dto.ConfigDTO
import io.choerodon.manager.api.dto.CreateConfigDTO
import io.choerodon.manager.api.dto.ItemDto
import io.choerodon.manager.app.service.ConfigService
import io.choerodon.manager.domain.manager.entity.ServiceE
import io.choerodon.manager.domain.repository.ServiceRepository
import io.choerodon.manager.infra.common.utils.config.ConfigUtil
import io.choerodon.mybatis.pagehelper.domain.PageRequest
import io.choerodon.mybatis.pagehelper.domain.Sort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Shared
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
    @Autowired
    private ServiceRepository serviceRepository
    @Shared
    ServiceE serviceE
    @Shared
    ServiceE serviceEforDel
    @Shared
    CreateConfigDTO testDataCreateConfig
    @Shared
    CreateConfigDTO configForDel
    @Shared
    List<CreateConfigDTO> testCreateConfigList
    @Shared
    ConfigDTO configDTO
    @Shared
    ConfigDTO dtofrodel
    @Shared
    PageRequest pageRequest
    @Shared
    List<ConfigCheckDTO> checkDTOList
    @Shared
    ItemDto item

    void setupSpec() {

        serviceE = new ServiceE()
        serviceE.setName("test_service")

        serviceEforDel = new ServiceE()
        serviceEforDel.setName("test2_service")

        testDataCreateConfig = new CreateConfigDTO()
        testDataCreateConfig.setName("test1")
        testDataCreateConfig.setServiceName("test_service")
        testDataCreateConfig.setVersion("v1")
        testDataCreateConfig.setYaml("test: test")

        configForDel = new CreateConfigDTO()
        configForDel.setName("del1")
        configForDel.setServiceName("test2_service")
        configForDel.setVersion("verison1")
        configForDel.setYaml("test: del")

        dtofrodel = new ConfigDTO()

        CreateConfigDTO data1 = new CreateConfigDTO()
        data1.setName("test2")
        data1.setServiceName("noExistService")
        data1.setVersion("v1")
        data1.setYaml("test: noExistService")

        CreateConfigDTO data2 = new CreateConfigDTO()
        data2.setName("test3")
        data2.setServiceName("test_service")
        data2.setVersion("v1")
        data2.setYaml("test, noExistService")
        testCreateConfigList = new ArrayList<>()
        testCreateConfigList.add(testDataCreateConfig)
        testCreateConfigList.add(data1)
        testCreateConfigList.add(data2)

        configDTO = new ConfigDTO()


        checkDTOList = new ArrayList<>()
        ConfigCheckDTO duplicate = new ConfigCheckDTO()
        duplicate.setName(configDTO.getName())
        duplicate.setServiceName(testDataCreateConfig.getServiceName())
        duplicate.setConfigVersion(configDTO.getConfigVersion())

        ConfigCheckDTO nullServiceName = new ConfigCheckDTO()
        nullServiceName.setName(configDTO.getName())
        nullServiceName.setServiceName(null)
        nullServiceName.setConfigVersion(configDTO.getConfigVersion())

        checkDTOList.add(duplicate)
        checkDTOList.add(nullServiceName)

        pageRequest = new PageRequest()
        pageRequest.setPage(1)
        pageRequest.setSize(10)
        pageRequest.setSort(new Sort('id'))


        item = new ItemDto()
        item.setValue("testValue")
        item.setProperty("testItem")
    }

    def "create"() {
        given: '创建测试用服务'
        serviceE = serviceRepository.addService(serviceE)

        when: '为服务创建配置 '
        configDTO = configService.create(testDataCreateConfig)
        then: '校验 并 删除测试用服务'
        noExceptionThrown()
        configDTO.getId() != null
        serviceRepository.deleteService(serviceE.getId())
    }

    def "create[Exception]"() {
        given: '创建测试用服务'
        serviceE = serviceRepository.addService(serviceE)

        when: '为服务创建配置 - 异常 '
        configService.create(createConfigDTO)
        then: '校验 并 删除测试用服务'
        def error = thrown(expectedException)
        error.message == expectedMessage
        serviceRepository.deleteService(serviceE.getId())

        where: '结果比对'
        createConfigDTO             || expectedException | expectedMessage
        testCreateConfigList.get(1) || CommonException   | 'error.config.serviceName.notExist'
        testCreateConfigList.get(2) || CommonException   | 'error.config.yml'
    }

    def "queryByServiceNameAndConfigVersion"() {
        given: '创建测试用服务'
        serviceE = serviceRepository.addService(serviceE)

        when: '根据服务名与版本号查询配置 '
        def queryConfig = configService.queryByServiceNameAndConfigVersion(testDataCreateConfig.getServiceName(), testDataCreateConfig.getVersion())
        then: '分析配置是否正确'
        noExceptionThrown()
        queryConfig.configVersion.equals(testDataCreateConfig.getVersion())

        when: '根据服务名与版本号查询配置 - 异常'
        configService.queryByServiceNameAndConfigVersion(name, version)
        then: '分析配置是否正确'
        def error = thrown(expectedException)
        error.message == expectedMessage
        serviceRepository.deleteService(serviceE.getId())

        where: '分析异常是否正确'
        name   | version         || expectedException | expectedMessage
        'test' | 'testException' || CommonException   | "error.serviceConfigDO.query.serviceNameOrConfigVersionNotFound"

    }

    def "query"() {
        given: '创建测试用服务'
        serviceE = serviceRepository.addService(serviceE)

        when: '根据配置Id，type查询配置'
        def query = configService.query(configDTO.getId(), 'properties')
        then: '分析查询所得的配置信息'
        noExceptionThrown()
        query.txt.equals(ConfigUtil.convertMapToText(configDTO.value, 'properties'))

        when: '根据配置Id，type查询配置——配置不存在'
        configService.query(0L, 'properties')
        then: '分析查询所得的配置信息'
        def error1 = thrown(CommonException)
        error1.message == "error.config.not.exist"
        serviceRepository.deleteService(serviceE.getId())

        when: '根据配置Id，type查询配置——服务不存在'
        configService.query(configDTO.getId(), 'properties')
        then: '分析查询所得的配置信息'
        def error2 = thrown(CommonException)
        error2.message == "error.service.notExist"
    }

    def "queryYaml"() {
        given: '创建测试用服务'
        serviceE = serviceRepository.addService(serviceE)

        when: '根据配置Id，查询yaml'
        def yaml = configService.queryYaml(configDTO.getId())
        then: '解析yaml文件'
        noExceptionThrown()
        yaml.yaml.equals(ConfigUtil.convertMapToText(configDTO.value, 'yaml'))

        when: '根据配置Id，type查询配置——配置不存在'
        configService.queryYaml(0L)
        then: '分析查询所得的配置信息'
        def error1 = thrown(CommonException)
        error1.message == "error.config.not.exist"
        serviceRepository.deleteService(serviceE.getId())

        when: '根据配置Id，type查询配置——服务不存在'
        configService.queryYaml(configDTO.getId())
        then: '分析查询所得的配置信息'
        def error2 = thrown(CommonException)
        error2.message == "error.config.service.not.exist"


    }

    def "listByServiceName"() {
        given: '指定查询所需的服务名'
        serviceE = serviceRepository.addService(serviceE)
        def queryServiceName = serviceE.getName()
        def queryInfo = new ConfigDTO()
        when: '列出指定服务下配置'
        def pagelist = configService.listByServiceName(queryServiceName, pageRequest, queryInfo, null)
        then: '配置列表不为空'
        noExceptionThrown()
        pagelist.isEmpty()
        serviceRepository.deleteService(serviceE.getId())
    }

    def "setServiceConfigDefault"() {
        given: '指定需要设置默认的配置Id'
        def configId = configDTO.getId()
        when: '根据配置Id将配置设为默认'
        configDTO = configService.setServiceConfigDefault(configId)
        then: '解析配置是否是默认'
        noExceptionThrown()
        configDTO.isDefault
    }

    def "queryDefaultByServiceName"() {
        given: '创建测试用服务'
        serviceE = serviceRepository.addService(serviceE)

        when: '指定查询所需的服务名'
        configService.queryDefaultByServiceName(serviceE.getName())
        then: '测试默认配置不为空'
        noExceptionThrown()

        when: '指定查询所需的服务名'
        configService.queryDefaultByServiceName(name)
        then: '测试默认配置不为空'
        def error = thrown(expectedException)
        error.message == expectedMessage
        serviceRepository.deleteService(serviceE.getId())

        where: '结果比对'
        name           || expectedException | expectedMessage
        'testNotExist' || CommonException   | 'error.serviceConfigDO.query.serviceNameNotFound'
    }


    def "update"() {
        given: '准备需更新的配置'
        def versionNum = configDTO.getObjectVersionNumber()
        configDTO.setName('update1')
        when: '更新配置'
        configDTO = configService.update(configDTO.getId(), configDTO)
        then: '解析更新后的配置'
        noExceptionThrown()
        configDTO.getName() == 'update1'
        configDTO.objectVersionNumber == versionNum + 1

    }

    def "updateConfig"() {
        given: '准备需更新的配置'
        configDTO.setName('update2')
        when: '更新配置'
        def update = configService.updateConfig(configDTO.getId(), configDTO, 'yaml')
        then: '解析更新后的配置'
        noExceptionThrown()
        update.name == 'update2'
        update.objectVersionNumber == configDTO.objectVersionNumber + 1

    }

    def "check"() {
        given: '创建测试用服务'
        serviceE = serviceRepository.addService(serviceE)

        when: '配置校验'
        configService.check(checkDTOList.get(0))
        then: '校验'
        noExceptionThrown()

        when: '配置校验'
        configService.check(checkBody)
        then: '校验'
        def error = thrown(expectedException)
        error.message == expectedMessage
        serviceRepository.deleteService(serviceE.getId())

        where: '结果比对'
        checkBody           || expectedException | expectedMessage
//        checkDTOList.get(0) || CommonException   | 'error.config.insert.versionDuplicate'
        checkDTOList.get(1) || CommonException   | 'error.config.serviceName.notExist'

    }

    def "saveItem"() {
        given: '创建测试用服务'
        serviceE = serviceRepository.addService(serviceE)
        when: '增加配置项'
        item = configService.saveItem(configDTO.getId(), item)
        then: '校验增加配置项'
        noExceptionThrown()

        when: '增加配置项'
        serviceRepository.deleteService(serviceE.getId())
        item = configService.saveItem(configDTO.getId(), item)
        then: '校验增加配置项——异常'
        def error1 = thrown(CommonException)
        error1.message == "error.service.notExist"

    }

    def "deleteItem"() {
        given: '创建测试用服务'
        serviceE = serviceRepository.addService(serviceE)
        when: '删除配置项'
        configService.deleteItem(configDTO.getId(), item.getProperty())
        then: '结果校验'
        noExceptionThrown()
        serviceRepository.deleteService(serviceE.getId())

    }

    def "deleteItem[Exception]"() {
        given: '创建测试用服务'
        serviceE = serviceRepository.addService(serviceE)

        when: '删除配置项-异常'
        configService.deleteItem(configDTO.getId(), property)
        then: '结果校验'
        def error = thrown(expectedException)
        error.message == expectedMessage
        serviceRepository.deleteService(serviceE.getId())

        where: '结果比对'
        property       || expectedException | expectedMessage
        ''             || CommonException   | 'error.config.item.update'
        'testNotExist' || CommonException   | 'error.config.item.not.exist'

    }

    def "delete"() {
        given: '指定要删除的Config 创建测试用服务'
        serviceEforDel = serviceRepository.addService(serviceEforDel)
        dtofrodel = configService.create(configForDel)
        def deleteId = dtofrodel.getId()
        when: '根据ConfigId删除配置'
        def delete = configService.delete(deleteId)
        then: '解析删除结果'
        noExceptionThrown()
        delete
        serviceRepository.deleteService(serviceEforDel.getId())
    }
}
