package io.choerodon.manager.infra.repository.impl

import io.choerodon.core.convertor.ConvertHelper
import io.choerodon.core.exception.CommonException
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.api.dto.ServiceDTO
import io.choerodon.manager.domain.manager.entity.ServiceE
import io.choerodon.manager.domain.repository.ServiceRepository
import io.choerodon.manager.infra.dataobject.ServiceDO
import io.choerodon.manager.infra.mapper.ServiceMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class ServiceRepositoryImplSpec extends Specification {

    @Autowired
    private ServiceRepository serviceRepository
    @Shared
    private ServiceE serviceE
    //@Shared
    private ServiceMapper mockServiceMapper = Mock(ServiceMapper)
    //@Shared
    private ServiceRepository mockServiceRepository = new ServiceRepositoryImpl(mockServiceMapper)

    def setupSpec() {
        serviceE = new ServiceE()
        serviceE.setName("repository-service")
        serviceE.setObjectVersionNumber(1)
    }

    def "GetService"() {
    }

    def "AddService"() {
        when: "正常添加"
        def insertedServiceE = serviceRepository.addService(serviceE)
        then: "校验结果"
        noExceptionThrown()
        serviceRepository.deleteService(insertedServiceE.getId())

        when: "异常添加，mock"
        mockServiceRepository.addService(serviceE)
        then:
        1 * mockServiceMapper.insert(_) >> { 2 }
        def error = thrown(expectedException)
        error.message == expectedMessage

        where: "异常对比"
        errorServiceE || expectedException | expectedMessage
        serviceE      || CommonException   | "error.service.add"
    }

    def "UpdateService"() {
        given: "新增并构建更新ServiceE"
        def updatedServiceE = serviceRepository.addService(serviceE)
        updatedServiceE.setName("repository1-service")
        updatedServiceE.setObjectVersionNumber(2)

        when: "正常更新"
        serviceRepository.updateService(updatedServiceE)
        then:
        noExceptionThrown()

        when: "异常更新，mock"
        mockServiceRepository.updateService(errorServiceE)
        then:
        1 * mockServiceMapper.selectByPrimaryKey(_) >> { ConvertHelper.convert(updatedServiceE, ServiceDO) }
        1 * mockServiceMapper.updateByPrimaryKeySelective(_) >> { 2 }
        def error = thrown(expectedException)
        error.message == expectedMessage
        serviceRepository.deleteService(updatedServiceE.getId())

        where: "异常对比"
        errorServiceE || expectedException | expectedMessage
        serviceE      || CommonException   | "error.service.update"

    }

    def "DeleteService"() {
        when: "异常删除，mock,测ServiceConverter"
        def serviceDTO = ConvertHelper.convert(serviceE, ServiceDTO)
        //提高覆盖率
        ConvertHelper.convert(serviceDTO, ServiceE)
        ConvertHelper.convert(ConvertHelper.convert(serviceDTO, ServiceDO), ServiceDTO)
        mockServiceRepository.deleteService(errorServiceE.getId())

        then:
        1 * mockServiceMapper.deleteByPrimaryKey(_) >> { 2 }
        def error = thrown(expectedException)
        error.message == expectedMessage

        where: "异常对比"
        errorServiceE || expectedException | expectedMessage
        serviceE      || CommonException   | "error.service.delete"
    }

    def "GetAllService"() {
    }

    def "GetService1"() {
    }

    def "SelectServicesByFilter"() {
    }
}
