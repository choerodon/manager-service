package io.choerodon.manager

import io.choerodon.manager.domain.repository.RouteRepository
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import spock.mock.DetachedMockFactory

/**
 * @author dengyouquan
 **/
@TestConfiguration
class MockBeanTestConfiguration {
    private final detachedMockFactory = new DetachedMockFactory()

    @Bean("mockRouteRepository")
    @Primary
    RouteRepository routeRepository(){
        return detachedMockFactory.Mock(RouteRepository)
    }
}
