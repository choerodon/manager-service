package io.choerodon.manager

import io.choerodon.eureka.event.EurekaEventPayload
import io.choerodon.manager.domain.repository.RouteRepository
import io.choerodon.manager.domain.service.IDocumentService
import io.choerodon.manager.domain.service.IRouteService
import io.choerodon.manager.domain.service.ISwaggerRefreshService
import org.mockito.ArgumentMatcher
import org.mockito.Mockito
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import spock.mock.DetachedMockFactory

/**
 * @author dengyouquan
 * */
@TestConfiguration
class MockBeanTestConfiguration {
    private final detachedMockFactory = new DetachedMockFactory()

    @Bean("mockRouteRepository")
    @Primary
    RouteRepository routeRepository() {
        return detachedMockFactory.Mock(RouteRepository)
    }

    /*@Bean("mockIDocumentService")
    @Primary
    IDocumentService iDocumentService() {
        return detachedMockFactory.Mock(IDocumentService)
    }*/

    @Bean("mockIDocumentService")
    @Primary
    IDocumentService iDocumentService() {
        IDocumentService mockIDocumentService = Mockito.mock(IDocumentService)
        def file = new File(this.class.getResource('/swagger.json').toURI())
        //Mockito.doReturn(file.getText("UTF-8")).when(mockIDocumentService).fetchSwaggerJsonByIp(Mockito.any(RegisterInstancePayload))
        Mockito.doReturn(file.getText("UTF-8")).when(mockIDocumentService).fetchSwaggerJsonByIp(Mockito.argThat(new ArgumentMatcher<EurekaEventPayload>() {
            @Override
            boolean matches(EurekaEventPayload eurekaEventPayload) {
                return !"test".equals(((EurekaEventPayload) argument).getAppName())
            }
        }))
        return mockIDocumentService
    }

    @Bean("mockSwaggerRefreshService")
    @Primary
    ISwaggerRefreshService swaggerRefreshService() {
        return detachedMockFactory.Mock(ISwaggerRefreshService)
    }

    @Bean("mockIRouteService")
    @Primary
    IRouteService iRouteService() {
        return detachedMockFactory.Mock(IRouteService)
    }
}
