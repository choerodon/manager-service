package io.choerodon.manager.api.controller.v1

import io.choerodon.manager.api.dto.MenuClickDTO
import io.choerodon.manager.app.service.ApiService
import io.choerodon.manager.app.service.impl.StatisticServiceImpl
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import spock.lang.Specification

class StatisticControllerSpec extends Specification {
    def "SaveMenuClick"() {
        given:
        MenuClickDTO menuClickDTO = new MenuClickDTO()
        menuClickDTO.setLevel("site")
        MenuClickDTO.Menu menu = new MenuClickDTO.Menu()
        menu.setName("菜单")
        menu.setCode("menu")
        menu.setCount(50)
        List<MenuClickDTO.Menu> menus = new ArrayList<>()
        menus << menu
        menuClickDTO.setMenus(menus)
        List<MenuClickDTO> menuClickDTOList = new ArrayList<>()
        menuClickDTOList << menuClickDTO

        StringRedisTemplate redisTemplate = Mock(StringRedisTemplate)
        redisTemplate.opsForZSet() >> Mock(ZSetOperations)
        ApiService apiService = Mock(ApiService)

        StatisticServiceImpl statisticService = new StatisticServiceImpl(redisTemplate, apiService)

        when: "没有redis key"
        StatisticController statisticController = new StatisticController(statisticService)
        def result = statisticController.saveMenuClick(menuClickDTOList)

        then:
        1 * redisTemplate.hasKey(_) >> false
        result.statusCode.is2xxSuccessful()

        when: "有redis key"
        def result1 = statisticController.saveMenuClick(menuClickDTOList)

        then:
        1 * redisTemplate.hasKey(_) >> true
        result1.statusCode.is2xxSuccessful()
    }

    def "QueryMenuClick"() {
        given:
        StringRedisTemplate redisTemplate = Mock(StringRedisTemplate)
        ApiService apiService = Mock(ApiService)
        StatisticServiceImpl statisticService = new StatisticServiceImpl(redisTemplate, apiService)
        StatisticController statisticController = new StatisticController(statisticService)


        when:
        def result = statisticController.queryMenuClick("2018-01-07", "2018-01-10", "site")

        then:
        result.statusCode.is2xxSuccessful()
        1 * apiService.queryInvokeCount(_, _, _, _, _) >> new HashMap<String, Object>()
    }
}
