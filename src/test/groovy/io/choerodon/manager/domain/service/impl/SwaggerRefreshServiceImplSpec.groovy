package io.choerodon.manager.domain.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.core.exception.CommonException
import io.choerodon.eureka.event.EurekaEventPayload
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.app.service.VersionStrategy
import io.choerodon.manager.app.service.impl.DefaultVersionStrategy
import io.choerodon.manager.app.service.impl.SwaggerRefreshServiceImpl
import io.choerodon.manager.infra.dto.SwaggerDTO
import io.choerodon.manager.infra.mapper.SwaggerMapper
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class SwaggerRefreshServiceImplSpec extends Specification {

    private ObjectMapper objectMapper = new ObjectMapper()
    private SwaggerMapper mockSwaggerMapper = Mock(SwaggerMapper)
    private VersionStrategy versionStrategy = new DefaultVersionStrategy()

    private SwaggerRefreshServiceImpl iSwaggerRefreshService

    def setup() {
        iSwaggerRefreshService = new SwaggerRefreshServiceImpl(mockSwaggerMapper, versionStrategy)
    }

    def "UpdateOrInsertSwagger"() {
        given: "构造请求参数"
        def payloadJson = '{"status":"UP","appName":"manager","version":"1.0","instanceAddress":"127.0.0.1"}'
        def registerInstancePayload = objectMapper.readValue(payloadJson, EurekaEventPayload)
        def errorPayloadJson = '{"status":"UP","appName":"error","version":"1.0","instanceAddress":"127.0.0.1"}'
        def errorRegisterInstancePayload = objectMapper.readValue(errorPayloadJson, EurekaEventPayload)
        def json = "json"
        def swaggerDO = new SwaggerDTO()
        swaggerDO.setServiceName("manager")
        swaggerDO.setServiceVersion("null_version")
        def swaggerDO1 = new SwaggerDTO()
        swaggerDO1.setServiceName("error")
        swaggerDO1.setServiceVersion("0.9")

        when: "调用方法"
        iSwaggerRefreshService.updateOrInsertSwagger(registerInstancePayload, json)
        iSwaggerRefreshService.updateOrInsertSwagger(errorRegisterInstancePayload, json)

        then: "校验调用次数"
        thrown CommonException
    }

}
