package io.choerodon.manager.app.service.impl

import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.api.dto.ConfigDTO
import io.choerodon.manager.api.dto.CreateConfigDTO
import io.choerodon.manager.app.service.ConfigService
import io.choerodon.manager.domain.manager.entity.ServiceE
import io.choerodon.manager.domain.repository.ServiceRepository
import io.choerodon.manager.infra.common.utils.config.ConfigUtil
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
class ConfigServiceImplSpec extends Specification {
    @Autowired
    ConfigService configService
    @Autowired
    private ServiceRepository serviceRepository
    @Shared
    ServiceE serviceE
    @Shared
    CreateConfigDTO createConfig
    @Shared
    ConfigDTO configDTO
    @Shared
    PageRequest pageRequest

    void setupSpec() {

        serviceE = new ServiceE()
        serviceE.setName("test_service")

        createConfig = new CreateConfigDTO()
        createConfig.setName("test1")
        createConfig.setServiceName("test_service")
        createConfig.setVersion("v1")
        createConfig.setYaml("test: test")

        configDTO = new ConfigDTO()

        pageRequest = new PageRequest()
        pageRequest.setPage(1)
        pageRequest.setSize(10)
        pageRequest.setSort(new Sort('id'))
    }

    def "create"() {
        given: '准备创建配置所需的服务'
        serviceE = serviceRepository.addService(serviceE)
        when: '为服务创建配置'
        configDTO = configService.create(createConfig)
        then: '查询创建的配置'
        configDTO.id != null
        configDTO.name.equals(this.createConfig.name)
    }

    def "queryByServiceNameAndConfigVersion"() {
        given: '指定查询所需的服务名与版本'
        def name = createConfig.getServiceName()
        def version = createConfig.getVersion()
        when: '根据服务名与版本号查询配置'
        def queryConfig = configService.queryByServiceNameAndConfigVersion(name, version)
        then: '分析配置是否正确'
        queryConfig.id != null
        queryConfig.configVersion.equals(version)
    }

    def "query"() {
        given: '指定查询所需的配置Id与类型'
        def configId = configDTO.getId()
        def type = 'properties'
        when: '根据配置Id，type查询配置'
        def query = configService.query(configId, type)
        then: '分析查询所得的配置信息'
        query.id.equals(configDTO.id)
        query.txt.equals(ConfigUtil.convertMapToText(configDTO.value, type))
    }

    def "queryYaml"() {
        given: '指定查询所需的配置Id'
        def configId = configDTO.getId()
        when: '根据配置Id，查询yaml'
        def yaml = configService.queryYaml(configId)
        then: '解析yaml文件'
        yaml.yaml.equals(ConfigUtil.convertMapToText(configDTO.value, 'yaml'))
    }

    def "listByServiceName"() {
//        given: '指定查询所需的服务名'
//        def queryServiceName = serviceE.getName()
//        when: '列出指定服务下配置'
//        def pagelist = configService.listByServiceName(queryServiceName, pageRequest, null, null)
//        then: '配置列表不为空'
//        !pagelist.isEmpty()
    }

    def "setServiceConfigDefault"() {
        given: '指定需要设置默认的配置Id'
        def configId = configDTO.getId()
        when: '根据配置Id将配置设为默认'
        configDTO = configService.setServiceConfigDefault(configId)
        then: '解析配置是否是默认'
        configDTO.isDefault
    }

    def "queryDefaultByServiceName"() {
//        given: '指定查询所需的服务名'
//        def name = serviceE.getName()
//        when: '根据服务名查询默认配置'
//        def createConfig = configService.queryDefaultByServiceName(name)
//        then: '测试默认配置不为空'
//        createConfig.isDefault == true
    }


    def "update"() {}

    def "updateConfig"() {}

    def "check"() {}

    def "saveItem"() {}

    def "deleteItem"() {}

    def "delete"() {}
}
