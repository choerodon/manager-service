package io.choerodon.manager.infra.repository.impl

import io.choerodon.core.exception.CommonException
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.domain.manager.entity.ServiceE
import io.choerodon.manager.domain.repository.ConfigRepository
import io.choerodon.manager.domain.repository.ServiceRepository
import io.choerodon.manager.infra.dataobject.ConfigDO
import io.choerodon.mybatis.pagehelper.domain.PageRequest
import io.choerodon.mybatis.pagehelper.domain.Sort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ConfigRepositoryImplSpec extends Specification {
    @Autowired
    ConfigRepository configRepository
    @Autowired
    ServiceRepository serviceRepository
    @Shared
    ServiceE sharedServiceE
    @Shared
    ConfigDO sharedConfigDO

    def "Create"() {
        given: '准备参数'
        def configDO = new ConfigDO()
        configDO.setName("testConfigDO")
        configDO.setValue('{"testValue":"testValue"}')
        configDO.setConfigVersion("testConfigVersion")

        and: '创建相应服务'
        ServiceE serviceE = new ServiceE()
        serviceE.setName("test_service")
        sharedServiceE = serviceRepository.addService(serviceE)
        configDO.setServiceId(sharedServiceE.getId())

        when: '方法调用'
        sharedConfigDO = configRepository.create(configDO)
        then: '结果分析'
        noExceptionThrown()
        sharedConfigDO.getId() != null
    }

    def "Update[Exception]"() {
        given: '准备参数'
        def configDOWithOutOVN = new ConfigDO()
        configDOWithOutOVN.setValue(sharedConfigDO.getValue())
        configDOWithOutOVN.setName("update_service")

        def configDO = new ConfigDO()
        configDO.setValue(sharedConfigDO.getValue())
        configDO.setName("update_service")
        configDO.setObjectVersionNumber(sharedConfigDO.getObjectVersionNumber())

        when: '方法调用'
        sharedConfigDO = configRepository.update(sharedConfigDO.getId(), configDOWithOutOVN)
        then: '结果分析'
        def e = thrown(CommonException)
        e.message == "error.objectVersionNumber.null"

        when: '方法调用'
        sharedConfigDO = configRepository.update(100L, configDO)
        then: '结果分析'
        def e2 = thrown(CommonException)
        e2.message == "error.config.item.not.exist"
    }

    def "Update"() {
        given: '准备参数'
        def configDO = new ConfigDO()
        configDO.setValue(sharedConfigDO.getValue())
        configDO.setName("updateConfigDO")
        configDO.setObjectVersionNumber(sharedConfigDO.getObjectVersionNumber())
        configDO.setConfigVersion("testConfigVersion")

        when: '方法调用'
        sharedConfigDO = configRepository.update(sharedConfigDO.getId(), configDO)
        then: '结果分析'
        noExceptionThrown()
        sharedConfigDO.getName().equals("updateConfigDO")
    }

    def "Query"() {
        when: '方法调用'
        configRepository.query(sharedConfigDO.getId())
        then: '结果分析'
        noExceptionThrown()
    }

    def "QueryByServiceNameAndConfigVersion"() {
        when: '方法调用'
        configRepository.queryByServiceNameAndConfigVersion(sharedServiceE.getName(), sharedConfigDO.getConfigVersion())
        then: '结果分析'
        noExceptionThrown()
    }

    def "QueryByServiceIdAndVersion"() {
        when: '方法调用'
        configRepository.queryByServiceIdAndVersion(sharedConfigDO.getServiceId(), sharedConfigDO.getConfigVersion())
        then: '结果分析'
        noExceptionThrown()
    }

    def "QueryByServiceIdAndName"() {
        when: '方法调用'
        configRepository.queryByServiceIdAndName(sharedConfigDO.getServiceId(), sharedConfigDO.getName())
        then: '结果分析'
        noExceptionThrown()
    }

    def "List"() {
        given: "构造pageRequest"
        def order = new Sort.Order("id")
        def pageRequest = new PageRequest(1, 20, new Sort(order))
        when: '方法调用'
        configRepository.list(pageRequest)
        then: '结果分析'
        noExceptionThrown()
    }

    def "ListByServiceName"() {
        given: "构造pageRequest"
        def order = new Sort.Order("id")
        def pageRequest = new PageRequest(1, 20, new Sort(order))

        when: '方法调用'
        configRepository.listByServiceName(sharedServiceE.getName(), pageRequest, sharedConfigDO, "")

        then: '结果分析'
        noExceptionThrown()
    }


    def "SetConfigDefault"() {
        when: '方法调用'
        sharedConfigDO = configRepository.setConfigDefault(sharedConfigDO.getId())
        then: '结果分析'
        noExceptionThrown()

        when: '方法调用'
        sharedConfigDO = configRepository.setConfigDefault(sharedConfigDO.getId())
        then: '结果分析'
        def e2 = thrown(CommonException)
        e2.message == "error.config.set.default"

        when: '方法调用'
        sharedConfigDO = configRepository.setConfigDefault(100L)
        then: '结果分析'
        def e = thrown(CommonException)
        e.message == "error.config.item.not.exist"
    }

    def "QueryDefaultByServiceName"() {
        when: '方法调用'
        def configDTO = configRepository.queryDefaultByServiceName(sharedServiceE.getName())
        then: '结果分析'
        configDTO.getIsDefault() == true
        noExceptionThrown()
    }

    def "Delete"() {
        when: '方法调用'
        configRepository.delete(100L)
        then: '结果分析'
        def e1 = thrown(CommonException)
        e1.message == "error.config.item.not.exist"

        when: '方法调用'
        configRepository.delete(sharedConfigDO.getId())
        serviceRepository.deleteService(sharedServiceE.getId())
        then: '结果分析'
        def e = thrown(CommonException)
        e.message == "error.config.delete.default"
    }

}
