package io.choerodon.manager.api.eventhandler

import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.MockBeanTestConfiguration
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import([IntegrationTestConfiguration, MockBeanTestConfiguration])
class EurekaInstanceRegisteredListenerSpec extends Specification {

    @Autowired
    private EurekaInstanceRegisteredListener eurekaInstanceRegisteredListener

    def "Handle"() {
        given: "构造ConsumerRecord"
        def payloadJson = '{"status":"UP","appName":"manager","version":"1.0","instanceAddress":"127.0.0.1"}'
        def consumerRecord = new ConsumerRecord("topic", 0, 0, "key".getBytes("UTF-8"), payloadJson.getBytes("UTF-8"))
        def errorPayloadJson = 'error{"status":"UP","appName":"manager","version":"1.0","instanceAddress":"127.0.0.1"}'
        def errorConsumerRecord = new ConsumerRecord("topic", 0, 0, "key".getBytes("UTF-8"), errorPayloadJson.getBytes("UTF-8"))
        def downStatusPayloadJson = '{"status":"DOWN","appName":"manager","version":"1.0","instanceAddress":"127.0.0.1"}'
        def downStatusConsumerRecord = new ConsumerRecord("topic", 0, 0, "key".getBytes("UTF-8"), downStatusPayloadJson.getBytes("UTF-8"))
        def isSkipServicePayloadJson = '{"status":"UP","appName":"register-server","version":"1.0","instanceAddress":"127.0.0.1"}'
        def isSkipServiceConsumerRecord = new ConsumerRecord("topic", 0, 0, "key".getBytes("UTF-8"), isSkipServicePayloadJson.getBytes("UTF-8"))
        def returnNullPayloadJson = '{"status":"UP","appName":"test","version":"1.0","instanceAddress":"127.0.0.1"}'
        def returnNullConsumerRecord = new ConsumerRecord("topic", 0, 0, "key".getBytes("UTF-8"), returnNullPayloadJson.getBytes("UTF-8"))

        when: "正常调用handle方法"
        eurekaInstanceRegisteredListener.handle(consumerRecord)
        eurekaInstanceRegisteredListener.handle(errorConsumerRecord)
        eurekaInstanceRegisteredListener.handle(downStatusConsumerRecord)
        eurekaInstanceRegisteredListener.handle(isSkipServiceConsumerRecord)
        eurekaInstanceRegisteredListener.handle(returnNullConsumerRecord)

        then: "校验结果(异常全被捕获了)"
        noExceptionThrown()
    }
}
