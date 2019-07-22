package io.choerodon.manager.api.controller.v1

import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.app.service.RouteService
import io.choerodon.manager.infra.dto.RouteDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class RouteControllerSpec extends Specification {

    private static final routeDTOJson = '{"id":10,"name":"test","path":"/test/**","serviceId":"test-service"}'
    private ObjectMapper objectMapper = new ObjectMapper()
    private RouteService mockRouteService = Mock(RouteService)

    @Autowired
    private TestRestTemplate restTemplate

    @Autowired
    private RouteController routeController

    def setup() {
        routeController.setRouteService(mockRouteService)
    }

    def "List"() {
        given: "构造请求参数"
        def params = "params"
        def map = ["name": "test", "path": "/test/**", "serviceId": 1, "builtIn": false, "params": params]

        when: "调用分页查询路由信息接口"
        def entity = restTemplate.getForEntity("/v1/routes?name={name}&path={path}&serviceId={serviceId}&builtIn={builtIn}&params={params}"
                , String, map)

        then: "校验状态码和调用次数"
        entity.statusCode.is2xxSuccessful()
        1 * mockRouteService.list(_,_, _ as RouteDTO, params)
        0 * _
    }

    def "Create"() {
        given: "构造RouteDTO"
        def routeDTO = objectMapper.readValue(routeDTOJson, RouteDTO)

        when: "调用增加一个新路由接口"
        def entity = restTemplate.postForEntity("/v1/routes", routeDTO, String)

        then: "校验状态码和调用次数"
        entity.statusCode.is2xxSuccessful()
        1 * mockRouteService.create(_ as RouteDTO)
        0 * _
    }

    def "Update"() {
        given: "构造RouteDTO"
        def routeDTO = objectMapper.readValue(routeDTOJson, RouteDTO)

        when: "调用更新一个新路由接口"
        def entity = restTemplate.postForEntity("/v1/routes/{route_id}", routeDTO, String, routeDTO.getId())

        then: "校验状态码和调用次数"
        entity.statusCode.is2xxSuccessful()
        1 * mockRouteService.update(routeDTO.getId(), _ as RouteDTO)
        0 * _
    }

    def "Delete"() {
        given: "构建参数"
        def routeId = 1L
        def httpEntity = new HttpEntity<Object>()

        when: "调用删除一个新路由接口"
        def entity = restTemplate.exchange("/v1/routes/{route_id}", HttpMethod.DELETE, httpEntity, String, routeId)

        then: "校验状态码和调用次数"
        entity.statusCode.is2xxSuccessful()
        1 * mockRouteService.delete(routeId)
        0 * _
    }

    def "Check"() {
        given: "构造RouteDTO"
        def routeDTO = objectMapper.readValue(routeDTOJson, RouteDTO)

        when: "调用检查一个新路由接口"
        def entity = restTemplate.postForEntity("/v1/routes/check", routeDTO, String)

        then: "校验状态码和调用次数"
        entity.statusCode.is2xxSuccessful()
        1 * mockRouteService.checkRoute(_ as RouteDTO)
        0 * _
    }
}
