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
        ApiServiceImpl impl = new ApiServiceImpl(iDocumentService, routeMapper, iSwaggerService)
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
}
