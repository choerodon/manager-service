package io.choerodon.manager.api.controller.v1

import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.app.service.ApiService
import io.choerodon.manager.app.service.SwaggerService
import io.choerodon.manager.app.service.impl.ApiServiceImpl
import io.choerodon.manager.domain.service.IDocumentService
import io.choerodon.manager.domain.service.ISwaggerService
import io.choerodon.manager.infra.dataobject.RouteDO
import io.choerodon.manager.infra.mapper.RouteMapper
import io.choerodon.mybatis.pagehelper.domain.PageRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
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
class ApiControllerSpec extends Specification {
    @Autowired
    private TestRestTemplate restTemplate

    @Autowired
    private ApiController apiController

    private SwaggerService mockSwaggerService = Mock(SwaggerService)

    private ApiService mockApiService = Mock(ApiService)

    def setup() {
        apiController.setSwaggerService(mockSwaggerService)
        apiController.setApiService(mockApiService)
    }

    def "Resources"() {
        when: "调用查询不包含跳过的服务的路由列表接口"
        def entity = restTemplate.getForEntity("/v1/swaggers/resources", String)

        then: "校验状态码和调用次数"
        entity.statusCode.is2xxSuccessful()
        1 * mockSwaggerService.getSwaggerResource()
        0 * _
    }

    def "QueryByNameAndVersion"() {
        given: "构造请求参数"
        def serviceName = "manager"
        def version = "0.10.0"
        def map = ["service_prefix": serviceName, "version": version, "params": "params", "name": "name", "description": "description"]

        when: "调用查询服务接口"
        def entity = restTemplate.getForEntity("/v1/swaggers/{service_prefix}/controllers" +
                "?service_prefix={service_prefix}&version={version}&params={params}&name={name}&description={description}", String, map)

        then: "校验状态码和调用次数"
        entity.statusCode.is2xxSuccessful()
        1 * mockApiService.getControllers(serviceName, version, _ as PageRequest, _ as HashMap)
        0 * _
    }

    def "QueryPathDetail"() {
        given: '构造请求参数'
        def serviceName = "manager"
        def version = 'test_version'
        def operationId = '1'
        def name = "api"
        def map = ["service_prefix": serviceName, "version": version, "operation_id": operationId, "name": name]

        when: "调用根据path的url和method查询单个path接口"
        def entity = restTemplate.getForEntity("/v1/swaggers/{service_prefix}/controllers/{name}/paths" +
                "?version={version}&operation_id={operation_id}", String, map)

        then: "校验状态码和调用次数"
        entity.statusCode.is2xxSuccessful()
        1 * mockApiService.queryPathDetail(serviceName, version, name, operationId)
        0 * _
    }

    def "QueryInstancesAndApiCount"() {
        given:
        IDocumentService iDocumentService = Mock(IDocumentService)
        ISwaggerService iSwaggerService = Mock(ISwaggerService)
        RouteMapper routeMapper = Mock(RouteMapper)
        ApiServiceImpl impl = new ApiServiceImpl(iDocumentService, routeMapper, iSwaggerService, Mock(StringRedisTemplate))
        ApiController controller = new ApiController(null, impl)

        SwaggerResource swaggerResource = new SwaggerResource()
        swaggerResource.setName("manager:manager-service")
        swaggerResource.setLocation("/docs/manager?version=null_version")
        swaggerResource.setSwaggerVersion("2.0")
        List<SwaggerResource> resources = new ArrayList<>()
        resources << swaggerResource
        iSwaggerService.getSwaggerResource() >> resources
        def file = new File(this.class.getResource('/swagger.json').toURI())
        iDocumentService.fetchSwaggerJsonByService(_, _) >> { file.getText('UTF-8') }
        iDocumentService.expandSwaggerJson(_, _, _) >> { file.getText('UTF-8') }
        RouteDO routeDO = new RouteDO()
        routeDO.setName("mamager")
        routeDO.setServiceId("manager-service")
        routeMapper.selectOne(_) >> routeDO

        when:
        def entity = controller.queryInstancesAndApiCount()
        then:
        Integer.valueOf(entity.getBody().get("apiCounts").getAt(0)) == 25
    }

    def "QueryServiceInvoke"() {
        given:
        StringRedisTemplate redisTemplate = Mock(StringRedisTemplate)
        ISwaggerService iSwaggerService = Mock(ISwaggerService)
        ApiServiceImpl apiService = new ApiServiceImpl(null, null, iSwaggerService, redisTemplate)
        ApiController controller = new ApiController(null, apiService)
        List swaggerList = new ArrayList()
        SwaggerResource swaggerResource = Mock(SwaggerResource)
        swaggerList << swaggerResource
        iSwaggerService.getSwaggerResource() >> swaggerList
        swaggerResource.getName() >> "manager:manager-service"
        swaggerResource.getLocation() >> "/docs/manager?version=null_version"
        redisTemplate.opsForValue() >> Mock(ValueOperations)

        when:
        def result = controller.queryServiceInvoke("2018-11-02", "2018-11-05")
        def list = (Set) result.getBody().get("date")
        then:
        list.contains("2018-11-02") && list.contains("2018-11-05")
    }

    def "QueryApiInvoke"() {
        given:
        StringRedisTemplate redisTemplate = Mock(StringRedisTemplate)
        ApiServiceImpl apiService = new ApiServiceImpl(null, null, null, redisTemplate)
        ApiController controller = new ApiController(null, apiService)
        ValueOperations valueOperations = Mock(ValueOperations)
        redisTemplate.opsForValue() >> valueOperations
        valueOperations.get(_) >> "{\"/v1/swaggers/api/count:get\":1,\"/v1/swaggers/service_invoke/count:get\":18,\"/v1/swaggers/api_invoke/count:get\":14}"

        when:
        def result = controller.queryApiInvoke("2018-11-02", "2018-11-05", "manager-service")
        def date = (Set) result.getBody().get("date")
        def apis = (Set) result.getBody().get("apis")
        then:
        date.contains("2018-11-02") && date.contains("2018-11-05")
        apis.contains("/v1/swaggers/api_invoke/count:get")
    }

    def "QueryTreeMenu"() {
        given:
        ISwaggerService iSwaggerService = Mock(ISwaggerService)
        IDocumentService iDocumentService = Mock(IDocumentService)
        ApiServiceImpl apiService = new ApiServiceImpl(iDocumentService, null, iSwaggerService, null)
        ApiController controller = new ApiController(null, apiService)
        List<SwaggerResource> resources = new ArrayList<>()
        SwaggerResource resource = new SwaggerResource()
        resource.setName("manager:manager-service")
        resource.setLocation("/docs/manager?version=null_version")
        resources << resource
        iSwaggerService.getSwaggerResource() >> resources
        def file = new File(this.class.getResource('/swagger.json').toURI())
        iDocumentService.fetchSwaggerJsonByService(_, _) >> { file.getText('UTF-8') }

        when:
        controller.queryTreeMenu()
        then:
        true
    }
}
