package io.choerodon.manager.domain.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.domain.manager.entity.RouteE
import io.choerodon.manager.domain.service.IRouteService
import org.apache.commons.collections.map.MultiKeyMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification
import spock.mock.DetachedMockFactory

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author superlee
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ISwaggerServiceImplSpec extends Specification {

//    private final detachedMockFactory = new DetachedMockFactory()

    private ObjectMapper objectMapper = new ObjectMapper()

    @Autowired
    private ISwaggerServiceImpl iSwaggerService

//    def setup() {
//        iSwaggerService = detachedMockFactory.Mock(ISwaggerServiceImpl)
//    }

    def "GetSwaggerResource"() {
        given: "构造iRouteService，定义返回值"
        def iRouteService = Spy(IRouteServiceImpl, constructorArgs: [null, null])
        MultiKeyMap multiKeyMap = new MultiKeyMap()
        String routeJson = '{"id":1,"name":"manager","path":"/manager/**","serviceId":"manager-service","url":"null","stripPrefix":true,"retryable":null,"sensitiveHeaders":"null","customSensitiveHeaders":false,"helperService":"null","objectVersionNumber":1}'
        RouteE route = objectMapper.readValue(routeJson, RouteE)
        multiKeyMap.put("manager-service", "null_version", route)
        iRouteService.getAllRunningInstances() >> { multiKeyMap }
        iSwaggerService.setIRouteService(iRouteService)

        when: "调用"
        def list = iSwaggerService.getSwaggerResource()

        then: "返回List不为空"
        !list.isEmpty()
    }

    def "GetUiConfiguration"() {
    }

    def "GetSecurityConfiguration"() {
    }
}
