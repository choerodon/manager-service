package io.choerodon.manager.domain.manager.converter

import io.choerodon.manager.IntegrationTestConfiguration
import io.choerodon.manager.domain.manager.entity.ConfigE
import io.choerodon.manager.infra.dto.ConfigDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ConfigConverterSpec extends Specification {
    @Autowired
    ConfigConverter configConverter
    @Shared
    ConfigDTO configDTO
    @Shared
    ConfigE configE
    @Shared
    ConfigDTO configDO
    void setupSpec(){
        def name="test_name"
        def configVersion="test_version"
        def isDefault=false
        def source="org"
        def serviceId=1L
        def value=new HashMap<String,Object>()
        value.put("testvalue","testvalue")
        configDTO=new ConfigDTO(name,configVersion,isDefault,source)
        configDTO.setValue(value)
    }
    def "DtoToEntity"() {
        when:"方法调用"
        configE = configConverter.dtoToEntity(configDTO)
        then:'结果比对'
        noExceptionThrown()
    }

    def "EntityToDto"() {
        when:"方法调用"
        configConverter.entityToDto(configE)
        then:'结果比对'
        noExceptionThrown()
    }
    def "EntityToDo"() {
        when:"方法调用"
        configDO=configConverter.entityToDo(configE)
        then:'结果比对'
        noExceptionThrown()
    }

    def "DoToEntity"() {
        when:"方法调用"
        configConverter.doToEntity(configDO)
        then:'结果比对'
        noExceptionThrown()
    }


    def "DoToDto"() {
        when:"方法调用"
        configConverter.doToDto(configDO)
        then:'结果比对'
        noExceptionThrown()
    }

    def "DtoToDo"() {
        when:"方法调用"
        configConverter.dtoToDo(configDTO)
        then:'结果比对'
        noExceptionThrown()
    }
}
