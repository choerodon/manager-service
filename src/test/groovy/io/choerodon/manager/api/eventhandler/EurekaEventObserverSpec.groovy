package io.choerodon.manager.api.eventhandler

import io.choerodon.eureka.event.EurekaEventPayload
import io.choerodon.manager.domain.service.IDocumentService
import io.choerodon.manager.domain.service.IRouteService
import io.choerodon.manager.domain.service.ISwaggerRefreshService
import spock.lang.Specification

class EurekaEventObserverSpec extends Specification {
    def "test receiveUpEvent"() {
        given: 'mock RegisterInstanceService'
        def service = Mock(IDocumentService) {
            fetchSwaggerJsonByIp(_) >> '{}'
        }
        def service1 = Mock(ISwaggerRefreshService)
        def service2 = Mock(IRouteService)
        def observer = new EurekaEventObserver(service, service1, service2)

        when:
        observer.receiveUpEvent(new EurekaEventPayload())
        then:
        1 * service1.updateOrInsertSwagger(_, _)
        1 * service2.autoRefreshRoute(_)

    }
}
