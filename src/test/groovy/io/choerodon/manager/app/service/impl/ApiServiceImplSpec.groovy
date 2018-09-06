package io.choerodon.manager.app.service.impl

import io.choerodon.core.exception.CommonException
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.app.service.ApiService
import io.choerodon.manager.domain.service.IDocumentService
import io.choerodon.mybatis.pagehelper.domain.PageRequest
import io.choerodon.mybatis.pagehelper.domain.Sort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author superlee
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ApiServiceImplSpec extends Specification {
    @Autowired
    private ApiService apiService

    private IDocumentService mockIDocumentService = Mock(IDocumentService)

    def setup() {
        apiService = new ApiServiceImpl(mockIDocumentService)
    }

    def "GetControllers"() {
        given: "准备参数"
        def name = "manager"
        def version = "null_version"

        def map = new HashMap<String, Object>()
        map.put("params", null)
        map.put("name", null)
        map.put("description", null)

        and: "构造pageRequest"
        def order = new Sort.Order("id")
        def pageRequest = new PageRequest(0, 20, new Sort(order))

        and: 'mock getSwaggerJson方法'
        def file = new File(this.class.getResource('/swagger.json').toURI())
        mockIDocumentService.getSwaggerJson(_, _) >> { file.getText('UTF-8') }

        when: "方法调用"
        def list = apiService.getControllers(name, version, pageRequest, map)

        then: "结果分析"
        !list.isEmpty()
    }

    def "GetControllers[Exception]"() {
        given: "准备参数"
        def name = "manager"
        def version = "null_version"

        def map = new HashMap<String, Object>()
        map.put("params", null)
        map.put("name", null)
        map.put("description", null)

        and: "构造pageRequest"
        def order = new Sort.Order("id")
        def pageRequest = new PageRequest(0, 20, new Sort(order))

        and: 'mock getSwaggerJson方法'
        mockIDocumentService.getSwaggerJson(_, _) >> { throw new IOException("") }

        when: "【异常】方法调用"
        apiService.getControllers(name, version, pageRequest, map)

        then: "检测异常"
        def IOe = thrown(CommonException)
        IOe.message == "error.service.not.run"
    }

    def "GetControllers[IOException]"() {
        given: "准备参数"
        def name = "manager"
        def version = "null_version"

        def map = new HashMap<String, Object>()
        map.put("params", null)
        map.put("name", null)
        map.put("description", null)

        and: "构造pageRequest"
        def order = new Sort.Order("id")
        def pageRequest = new PageRequest(0, 20, new Sort(order))

        and: 'mock getSwaggerJson方法'
        mockIDocumentService.getSwaggerJson(_, _) >> { throw new IOException("") }

        when: "【异常】方法调用"
        apiService.getControllers(name, version, pageRequest, map)

        then: "检测异常"
        def IOe = thrown(CommonException)
        IOe.message == "error.service.not.run"
    }

    def "QueryPathDetail"() {
        given: "准备查询参数"
        def serviceName = "test"
        def version = "test"
        def controllerName_NotFound = "test"
        def controllerName = 'api-controller'
        def operationId = "test"

        and: 'mock iDocumentService.getSwaggerJson & objectMapper.readTree'
        def file = new File(this.class.getResource('/swagger.json').toURI())
        mockIDocumentService.getSwaggerJson(_, _) >> { file.getText('UTF-8') }

        when: '调用方法'
        apiService.queryPathDetail(serviceName, version, controllerName, operationId)

        then: '结果分析'
        noExceptionThrown()

        when: '调用方法'
        apiService.queryPathDetail(serviceName, version, controllerName_NotFound, operationId)

        then: '捕获异常'
        def error = thrown(CommonException)
        error.message == 'error.controller.not.found'
    }

    def "QueryPathDetail[Service Not Run]"() {
        given: "准备查询参数"
        def serviceName = "test"
        def version = "test"
        def controllerName = "test"
        def operationId = "test"
        and: 'mock iDocumentService.getSwaggerJson'
        mockIDocumentService.getSwaggerJson(_, _) >> { throw new IOException("") }

        when: '调用方法'
        apiService.queryPathDetail(serviceName, version, controllerName, operationId)

        then: '捕获异常'
        def error = thrown(CommonException)
        error.message == 'error.service.not.run'
    }
}
