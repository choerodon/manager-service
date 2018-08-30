package io.choerodon.manager.app.service.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.core.convertor.ConvertHelper
import io.choerodon.core.exception.CommonException
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.api.dto.RouteDTO
import io.choerodon.manager.domain.manager.entity.RouteE
import io.choerodon.manager.domain.repository.RouteRepository
import io.choerodon.manager.infra.dataobject.RouteDO
import io.choerodon.mybatis.pagehelper.domain.PageRequest
import io.choerodon.mybatis.pagehelper.domain.Sort
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class RouteServiceImplSpec extends Specification {

    @Shared
    private ObjectMapper objectMapper

    @Autowired
    private RouteServiceImpl routeService

    @Autowired
    private RouteRepository routeRepository

    @Shared
    def createdRouteDTO
    @Shared
    def nameDuplicateRouteDTO
    @Shared
    def pathDuplicateRouteDTO

    def setupSpec() {
        objectMapper = new ObjectMapper()
        String routeDTOJson = '{"id":10,"name":"testdyq","path":"/testdyq/**","serviceId":"testdyq-service"}'
        createdRouteDTO = objectMapper.readValue(routeDTOJson, RouteDTO)
        nameDuplicateRouteDTO = new RouteDTO()
        BeanUtils.copyProperties(createdRouteDTO, nameDuplicateRouteDTO)
        nameDuplicateRouteDTO.setName("iam")
        pathDuplicateRouteDTO = new RouteDTO()
        BeanUtils.copyProperties(createdRouteDTO, pathDuplicateRouteDTO)
        pathDuplicateRouteDTO.setPath("/iam/**")
    }

    def "List"() {
        given: "构建RouteDO和PageRequest"
        def pageRequest = new PageRequest(0, 10, new Sort("id"))
        def routeDO = new RouteDO()
        def params = ""

        when: "调用list方法"
        def page = routeService.list(pageRequest, routeDO, params)

        then: "分析结果"
        noExceptionThrown()
        !page.isEmpty()
    }

    def "Create"() {
        given: "创建正确的RouteDTO对象"
        def routeDTO = createdRouteDTO

        when: "调用创建routeDTO方法"
        def newRouteDTO = routeService.create(routeDTO)

        then: "校验删除RouteDTO"
        noExceptionThrown()
        newRouteDTO != null && newRouteDTO.getId() != null
        routeRepository.deleteRoute(ConvertHelper.convert(newRouteDTO, RouteE))
    }

    def "Update"() {
        given: "构造RouteDTO"
        def routeDTO = createdRouteDTO
        def newRouteDTO = routeService.create(routeDTO)
        newRouteDTO.setName("testupdate")
        newRouteDTO.setPath("/testupdate/**")

        when: "调用更新routeDTO方法"
        def updatedRouteDTO = routeService.update(newRouteDTO.getId(), newRouteDTO)

        then: "校验"
        noExceptionThrown()
        updatedRouteDTO != null && updatedRouteDTO.getId() != null
        routeRepository.deleteRoute(ConvertHelper.convert(updatedRouteDTO, RouteE))
    }

    def "Delete"() {
        given: "从数据库得到routeDTO"
        //def routeDTO = ConvertHelper.convert(routeRepository.getAllRoute().get(0), RouteDTO)
        def routeDTO = createdRouteDTO
        def newRouteDTO = routeService.create(routeDTO)

        when: "调用删除routeDTO方法"
        def isDeleteSuccess = routeService.delete(newRouteDTO.getId())

        then: "校验"
        noExceptionThrown()
        isDeleteSuccess
    }

    def "AddRoutesBatch"() {
        given: "创建正确的List<RouteDTO>对象"
        String routeDTOListJson = '[{"id":10,"name":"testdyq","path":"/testdyq/**","serviceId":"testdyq-service"}' +
                ',{"id":11,"name":"test1","path":"/testdyq1/**","serviceId":"testdyq1-service"}]'
        def routeDTOList = objectMapper.readValue(routeDTOListJson, new TypeReference<List<RouteDTO>>() {})

        when: "调用创建routeDTO方法"
        def createdRouteDTOList = routeService.addRoutesBatch(routeDTOList)

        then: "校验并删除List<RouteDTO>"
        noExceptionThrown()
        !createdRouteDTOList.isEmpty()
        routeRepository.deleteRoute(ConvertHelper.convert(routeDTOList.get(0), RouteE))
        routeRepository.deleteRoute(ConvertHelper.convert(routeDTOList.get(1), RouteE))
    }

    def "GetAllRoute"() {
        when: "调用获取所有routeDTO方法"
        def routeDTOList = routeService.getAllRoute()

        then: "校验"
        noExceptionThrown()
        !routeDTOList.isEmpty()
    }

    def "QueryByName"() {
        given: "构造RouteDTO"
        def routeDTO = ConvertHelper.convert(routeRepository.getAllRoute().get(0), RouteDTO)

        when: "调用通过名字查找routeDTO方法"
        def queryRouteDTO = routeService.queryByName(routeDTO.getName())

        then: "校验"
        noExceptionThrown()
        routeDTO.getName().equals(queryRouteDTO.getName())
    }

    def "CheckRoute"() {
        given: "构造Route"
        def routeDTO = createdRouteDTO

        when: "调用正常checkRoute方法"
        routeService.checkRoute(routeDTO)

        then: "校验"
        noExceptionThrown()

        when: "调用抛出异常的checkRoute方法"
        routeService.checkRoute(duplicateRouteDTO)

        then: "校验异常信息"
        def error = thrown(expectedException)
        error.message == expectedMessage

        where: "异常对比"
        duplicateRouteDTO     || expectedException | expectedMessage
        nameDuplicateRouteDTO || CommonException   | "error.route.insert.nameDuplicate"
        pathDuplicateRouteDTO || CommonException   | "error.route.insert.pathDuplicate"
    }
}
