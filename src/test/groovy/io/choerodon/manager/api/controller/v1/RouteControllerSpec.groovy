package io.choerodon.manager.api.controller.v1

import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.core.convertor.ConvertHelper
import io.choerodon.core.exception.CommonException
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.api.dto.RouteDTO
import io.choerodon.manager.domain.manager.entity.RouteE
import io.choerodon.manager.infra.repository.impl.RouteRepositoryImpl
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

    private ObjectMapper objectMapper = new ObjectMapper()

    @Autowired
    private TestRestTemplate restTemplate

    @Autowired
    private RouteRepositoryImpl routeRepository;

    def "List"() {
        when: "发送一个get请求"
        def entity = restTemplate.getForEntity("/v1/routes", String)

        then: "校验状态码"
        noExceptionThrown()
        entity.statusCode.is2xxSuccessful()
    }

    def "Create"() {
        given: "构造RouteDTO"
        String routeDTOJson = '{"id":10,"name":"testdyq","path":"/testdyq/**","serviceId":"testdyq-service"}'
        def routeDTO = objectMapper.readValue(routeDTOJson, RouteDTO)

        when: "发送一个post请求"
        def entity = restTemplate.postForEntity("/v1/routes", routeDTO, String)

        then: "校验状态码并删除routeDTO"
        noExceptionThrown()
        entity.statusCode.is2xxSuccessful()
        routeRepository.deleteRoute(ConvertHelper.convert(routeDTO, RouteE))
    }

    def "Update"() {
        given: "构造RouteDTO"
        //String routeDTOJson = '{"id":1,"name":"testdyq","path":"/testdyq/**","serviceId":"testdyq-service"}'
        //def routeDTO = objectMapper.readValue(routeDTOJson, RouteDTO)
        def routeDTO = ConvertHelper.convert(routeRepository.getAllRoute().get(0), RouteDTO)
        routeDTO.setName("testdyq")
        routeDTO.setPath("/testdyq/**")
        routeDTO.setBuiltIn(false)

        when: "发送一个post请求"
        def entity = restTemplate.postForEntity("/v1/routes/{route_id}", routeDTO, String, routeDTO.getId())

        then: "校验状态码"
        noExceptionThrown()
        entity.statusCode.is2xxSuccessful()
    }

    def "Delete"() {
        given:
        def routeDTO = ConvertHelper.convert(routeRepository.getAllRoute().get(0), RouteDTO)
        def url = "/v1/routes/{route_id}"
        HttpEntity<Object> httpEntity = new HttpEntity<>()

        when: "发送一个delete请求"
        def entity = restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, String, routeDTO.getId())

        then: "校验状态码"
        noExceptionThrown()
        entity.statusCode.is2xxSuccessful()

        /*when: "发送一个delete请求-异常"
        entity = restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, String, route_id)

        then: "抛出异常"
        entity.statusCode.is2xxSuccessful()
        def error = thrown(expectedException)
        error.message == expectedMessage

        where: "检查异常"
        route_id || expectedException | expectedMessage
        100      || CommonException   | "error.delete.route"*/

    }

    def "Check"() {
        given: "构造RouteDTO"
        def routeDTO = ConvertHelper.convert(routeRepository.getAllRoute().get(0), RouteDTO)
        routeDTO.setName("test1")
        routeDTO.setPath("/test1/**")

        when: "发送一个post请求"
        def entity = restTemplate.postForEntity("/v1/routes/check", routeDTO, String)

        then: "校验状态码"
        noExceptionThrown()
        entity.statusCode.is2xxSuccessful()
    }
}
