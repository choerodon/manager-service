package io.choerodon.manager.app.service.impl

import io.choerodon.core.exception.CommonException
import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.app.service.InstanceService
import io.choerodon.mybatis.pagehelper.domain.PageRequest
import io.choerodon.mybatis.pagehelper.domain.Sort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author Eugen
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class InstanceServiceImplSpec extends Specification {
    @Autowired
    InstanceService instanceService
    @Shared
    List<String> instanceIdList
    @Shared
    Long configId
    @Shared
    Map<String, Object> map
    @Shared
    PageRequest pageRequest

    void setupSpec() {
        instanceIdList = new ArrayList<>()
        instanceIdList.add('test-server:11.111.11.111:1111')
        instanceIdList.add('test:illegal')

        configId = 1L

        map = new HashMap<>()
        map.put("service", "")
        map.put("instanceId", "")
        map.put("version", "")
        map.put("status", "")
        map.put("params", "")

        pageRequest = new PageRequest()
        pageRequest.setPage(1)
        pageRequest.setSize(10)
        pageRequest.setSort(new Sort('instanceId'))
    }

    def "Query"() {
        when: '根据instanceId查询Instance'
        instanceService.query(instanceIdList.get(0))
        then: '结果分析'
        noExceptionThrown()

        when: '根据instanceId查询Instance-异常'
        instanceService.query(instanceId)
        then: '捕获异常'
        def error = thrown(expectedException)
        error.message == expectedMessage
        where: '结果比对'
        instanceId            || expectedException | expectedMessage
        instanceIdList.get(1) || CommonException   | 'error.illegal.instanceId'
    }

    def "Update"() {
        when: '更新实例'
        instanceService.update(instanceIdList.get(0), configId)
        then: '结果分析'
        noExceptionThrown()

        when: '更新实例-异常'
        instanceService.update(instanceId, configId)
        then: '捕获异常'
        def error = thrown(expectedException)
        error.message == expectedMessage
        where: '结果比对'
        instanceId            || expectedException | expectedMessage
        instanceIdList.get(1) || CommonException   | 'error.instance.updateConfig.badParameter'
    }

    def "ListByOptions"() {
        when: '查询实例列表'
        instanceService.listByOptions(map.get("service"), map, pageRequest)
        then: '结果分析'
        noExceptionThrown()
    }
}
