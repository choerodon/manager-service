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
    ApiService apiService

//    @Spy
//    private IDocumentService iDocumentService
//    @Autowired
//    @InjectMocks
//    private ApiService apiService

//    def setup() {
//        MockitoAnnotations.initMocks(this)
//    }

    def "GetControllers"() {
        given: "构造param map"
        Map<String, Object> map = new HashMap<>()
        map.put("params", null)
        map.put("name", null)
        map.put("description", null)
        and: "构造pageRequest"
        def order = new Sort.Order("id")
        def pageRequest = new PageRequest(0, 20, new Sort(order))
        and: "注入iDocumentService"
        def iDocumentService = Spy(IDocumentService)
        def file = new File(this.class.getResource('/swagger.json').toURI())
        iDocumentService.getSwaggerJson(_, _) >> { file.getText('UTF-8') }
        ApiServiceImpl apiServiceImpl = new ApiServiceImpl(iDocumentService)

        when: "调用请求"
        def list = apiServiceImpl.getControllers("manager", "null_version", pageRequest, map)

        then: "解析集合不为空"
        !list.isEmpty()


    }

    def "QueryPathDetail"() {
        when: '调用方法'
        apiService.queryPathDetail(serviceName, 'test', controllerName, 'test')
        then: '捕获异常'
        def error = thrown(expectedException)
        error.message == expectedMessage
        where: '结果比对'
        serviceName    | controllerName  || expectedException | expectedMessage
        'test_service' | 'ApiController' || CommonException   | 'error.service.not.run'
    }
}
