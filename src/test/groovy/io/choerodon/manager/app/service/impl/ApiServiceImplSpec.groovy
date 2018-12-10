package io.choerodon.manager.app.service.impl

import io.choerodon.core.exception.CommonException
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.api.dto.swagger.ParameterDTO
import io.choerodon.manager.api.dto.swagger.PathDTO
import io.choerodon.manager.app.service.ApiService
import io.choerodon.manager.domain.service.IDocumentService
import io.choerodon.manager.domain.service.ISwaggerService
import io.choerodon.manager.infra.mapper.RouteMapper
import io.choerodon.mybatis.pagehelper.domain.PageRequest
import io.choerodon.mybatis.pagehelper.domain.Sort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.StringRedisTemplate
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

    @Autowired
    ISwaggerService iSwaggerService
    @Autowired
    RouteMapper routeMapper

    def setup() {
        apiService = new ApiServiceImpl(mockIDocumentService, routeMapper, iSwaggerService, Mock(StringRedisTemplate))
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
        mockIDocumentService.fetchSwaggerJsonByService(_, _) >> { file.getText('UTF-8') }
        mockIDocumentService.expandSwaggerJson(_,_,_) >> { file.getText('UTF-8') }

        when: "方法调用"
        def list = apiService.getControllers(name, version, pageRequest, map)

        then: "结果分析"
        !list.isEmpty()
    }

//    def "GetControllers[Exception]"() {
//        given: "准备参数"
//        def name = "manager"
//        def version = "null_version"
//
//        def map = new HashMap<String, Object>()
//        map.put("params", null)
//        map.put("name", null)
//        map.put("description", null)
//
//        and: "构造pageRequest"
//        def order = new Sort.Order("id")
//        def pageRequest = new PageRequest(0, 20, new Sort(order))
//
//        and: 'mock getSwaggerJson方法'
//        mockIDocumentService.fetchSwaggerJsonByService(_, _) >> { throw new IOException("") }
//
//        when: "【异常】方法调用"
//        apiService.getControllers(name, version, pageRequest, map)
//
//        then: "检测异常"
//        def IOe = thrown(CommonException)
//        IOe.message == "java.io.IOException: "
//    }

//    def "GetControllers[IOException]"() {
//        given: "准备参数"
//        def name = "manager"
//        def version = "null_version"
//
//        def map = new HashMap<String, Object>()
//        map.put("params", null)
//        map.put("name", null)
//        map.put("description", null)
//
//        and: "构造pageRequest"
//        def order = new Sort.Order("id")
//        def pageRequest = new PageRequest(0, 20, new Sort(order))
//
//        and: 'mock getSwaggerJson方法'
//        mockIDocumentService.fetchSwaggerJsonByService(_, _) >> { throw new IOException("") }
//
//        when: "【异常】方法调用"
//        apiService.getControllers(name, version, pageRequest, map)
//
//        then: "检测异常"
//        def IOe = thrown(CommonException)
//        IOe.message == "java.io.IOException: "
//    }

    def "QueryPathDetail"() {
        given: "准备查询参数"
        def serviceName = "test"
        def version = "test"
        def controllerName_NotFound = "test"
        def controllerName = 'api-controller'
        def operationId = "test"

        and: "构造parameterDTO和pathDTO"
        def parameterDTO = new ParameterDTO()
        parameterDTO.setBody("test")
        def parameterDTO1 = new ParameterDTO()
        parameterDTO1.setBody("test1")
        def pathDTO = new PathDTO()
        pathDTO.setDescription("test")
        def pathDTO1 = new PathDTO()
        pathDTO1.setDescription("test1")

        and: 'mock iDocumentService.getSwaggerJson & objectMapper.readTree'
        def file = new File(this.class.getResource('/swagger.json').toURI())
        mockIDocumentService.fetchSwaggerJsonByService(_, _) >> { file.getText('UTF-8') }

        when: '调用方法'
        apiService.queryPathDetail(serviceName, version, controllerName, operationId)

        then: '结果分析'
        thrown(CommonException)

        when: '调用方法'
        apiService.queryPathDetail(serviceName, version, controllerName_NotFound, operationId)

        then: '捕获异常'
        def error = thrown(CommonException)
        error.message.contains('error.route.not.found')
    }

    def "QueryPathDetail[Service Not Run]"() {
        given: "准备查询参数"
        def serviceName = "test"
        def version = "test"
        def controllerName = "test"
        def operationId = "test"
        and: 'mock iDocumentService.getSwaggerJson'
        mockIDocumentService.fetchSwaggerJsonByService(_, _) >> { throw new IOException("") }

        when: '调用方法'
        apiService.queryPathDetail(serviceName, version, controllerName, operationId)

        then: '捕获异常'
        def error = thrown(CommonException)
        error.message.contains('error.route.not.found')
    }
}
