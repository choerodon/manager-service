package io.choerodon.manager.app.service.impl

import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.api.dto.swagger.ControllerDTO
import io.choerodon.manager.app.service.ApiService
import io.choerodon.manager.domain.service.IDocumentService
import io.choerodon.manager.domain.service.ISwaggerService
import io.choerodon.manager.infra.dataobject.RouteDO
import io.choerodon.manager.infra.dataobject.Sort
import io.choerodon.manager.infra.mapper.RouteMapper
import org.apache.poi.ss.formula.functions.Odd
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import spock.lang.Specification
import springfox.documentation.swagger.web.SwaggerResource

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


        and: 'mock getSwaggerJson方法'
        def file = new File(this.class.getResource('/swagger.json').toURI())
        mockIDocumentService.fetchSwaggerJsonByService(_, _) >> { file.getText('UTF-8') }
        mockIDocumentService.expandSwaggerJson(_, _, _) >> { file.getText('UTF-8') }

        when: "方法调用"
        Sort.Order order = new Sort.Order("id")
        Sort sort = new Sort(order)
        def list = apiService.getControllers(name, version, 1, 20, sort, map)

        then: "结果分析"
        !list.getList().isEmpty()
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
        given:
        ISwaggerService iSwaggerService = Mock(ISwaggerService)
        IDocumentService iDocumentService = Mock(IDocumentService)
        StringRedisTemplate stringRedisTemplate = Mock(StringRedisTemplate)
        RouteMapper routeMapper1 = Mock(RouteMapper)
        ApiServiceImpl apiService = new ApiServiceImpl(iDocumentService, routeMapper1, iSwaggerService, stringRedisTemplate)
        List<SwaggerResource> resources = new ArrayList<>()
        SwaggerResource resource = new SwaggerResource()
        resource.setName("manager:manager-service")
        resource.setLocation("/docs/manager?version=null_version")
        resources << resource
        iSwaggerService.getSwaggerResource() >> resources
        def file = new File(this.class.getResource('/swagger.json').toURI())
        iDocumentService.fetchSwaggerJsonByService(_, _) >> { file.getText('UTF-8') }
        iDocumentService.expandSwaggerJson(_, _, _) >> { file.getText('UTF-8') }
        RouteDO routeDO = Mock(RouteDO)
        routeMapper1.selectOne(_) >> routeDO
        routeDO.getServiceId() >> "manager-service"

        when:
        ControllerDTO value = apiService.queryPathDetail("manager", "null_version", "api-controller", "resourcesUsingGET")

        then:
        1 * stringRedisTemplate.hasKey(_) >> false
        1 * stringRedisTemplate.opsForValue() >> Mock(ValueOperations)
        value.getName() == "api-controller"
    }

    def "QueryPathDetail[from redis]"() {
        given:
        ISwaggerService iSwaggerService = Mock(ISwaggerService)
        IDocumentService iDocumentService = Mock(IDocumentService)
        StringRedisTemplate stringRedisTemplate = Mock(StringRedisTemplate)
        RouteMapper routeMapper1 = Mock(RouteMapper)
        ApiServiceImpl apiService = new ApiServiceImpl(iDocumentService, routeMapper1, iSwaggerService, stringRedisTemplate)
        List<SwaggerResource> resources = new ArrayList<>()
        SwaggerResource resource = new SwaggerResource()
        resource.setName("manager:manager-service")
        resource.setLocation("/docs/manager?version=null_version")
        resources << resource
        iSwaggerService.getSwaggerResource() >> resources
        def file = new File(this.class.getResource('/swagger.json').toURI())
        iDocumentService.fetchSwaggerJsonByService(_, _) >> { file.getText('UTF-8') }
        iDocumentService.expandSwaggerJson(_, _, _) >> { file.getText('UTF-8') }
        RouteDO routeDO = Mock(RouteDO)
        routeMapper1.selectOne(_) >> routeDO
        routeDO.getServiceId() >> "manager-service"
        ValueOperations valueOperations = Mock(ValueOperations)

        when:
        ControllerDTO value = apiService.queryPathDetail("manager", "null_version", "api-controller", "resourcesUsingGET")

        then:
        1 * stringRedisTemplate.hasKey(_) >> true
        1 * stringRedisTemplate.opsForValue() >> valueOperations
        1 * valueOperations.get(_) >> "{\"name\":\"api-controller\",\"description\":\"api测试\",\"paths\":[{\"url\":\"/v1/swaggers/resources\",\"method\":\"get\",\"consumes\":[\"application/json\"],\"produces\":[\"*/*\"],\"operationId\":\"resourcesUsingGET\",\"parameters\":[],\"responses\":[{\"httpStatus\":\"200\",\"description\":\"OK\",\"body\":\"[\\n{\\n\\\"swaggerVersion\\\":\\\"string\\\"\\n\\\"name\\\":\\\"string\\\"\\n\\\"location\\\":\\\"string\\\"\\n}\\n]\"},{\"httpStatus\":\"401\",\"description\":\"Unauthorized\",\"body\":null},{\"httpStatus\":\"403\",\"description\":\"Forbidden\",\"body\":null},{\"httpStatus\":\"404\",\"description\":\"Not Found\",\"body\":null}],\"remark\":\"查询不包含跳过的服务的路由列表\",\"description\":\"{\\\"permission\\\":{\\\"action\\\":\\\"resources\\\",\\\"menuLevel\\\":null,\\\"permissionLevel\\\":\\\"site\\\",\\\"roles\\\":[\\\"role/site/default/developer\\\"],\\\"permissionLogin\\\":false,\\\"permissionPublic\\\":false,\\\"permissionWithin\\\":false},\\\"label\\\":null}\",\"refController\":\"api-controller\",\"innerInterface\":false,\"basePath\":\"/manager\",\"code\":\"manager-service.api.resources\"}]}"
        value.getName() == "api-controller"
    }
}
