package io.choerodon.manager.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.MenuClickDTO;
import io.choerodon.manager.app.service.ApiService;
import io.choerodon.manager.app.service.StatisticService;
import io.choerodon.manager.infra.enums.InvokeCountBusinessType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author superlee
 */
@Service
public class StatisticServiceImpl implements StatisticService {

    private StringRedisTemplate redisTemplate;

    private ApiService apiService;

    private static final String COLON = ":";

    private static final String[] ROOT_CODES = {"choerodon.code.top.site", "choerodon.code.top.organization", "choerodon.code.top.project", "choerodon.code.top.user"};

    public StatisticServiceImpl(StringRedisTemplate redisTemplate, ApiService apiService) {
        this.redisTemplate = redisTemplate;
        this.apiService = apiService;
    }

    @Override
    public void saveMenuClick(List<MenuClickDTO> menuClickList) {

        menuClickList.forEach(menuClickDTO -> assertCode(menuClickDTO.getRootCode()));
        LocalDate localDate = LocalDate.now();
        menuClickList.forEach(menuClickDTO -> {
            String code = getCode(menuClickDTO.getRootCode());
            List<MenuClickDTO.Menu> menus = menuClickDTO.getMenus();
            StringBuilder builder = new StringBuilder();
            builder.append(localDate.toString()).append(COLON).append("zSet").append(COLON).append(code);
            String key = builder.toString();

            cache2Redis(menus, key);
        });
    }

    private String getCode(String code) {
        int index = code.lastIndexOf('.');
        return code.substring(index + 1);
    }

    @Override
    public Map<String, Object> queryMenuClick(String beginDate, String endDate, String code) {
        return apiService.queryInvokeCount(beginDate, endDate, getCode(code), "menu", Collections.emptySet(), InvokeCountBusinessType.MENU);
    }

    private void cache2Redis(List<MenuClickDTO.Menu> menus, String key) {
        if (redisTemplate.hasKey(key)) {
            menus.forEach(menu -> {
                int count = menu.getCount() == null ? 0 : menu.getCount();
                String value = menu.getCode();
                redisTemplate.opsForZSet().incrementScore(key, value, count);
            });
        } else {
            menus.forEach(menu -> {
                int count = menu.getCount() == null ? 0 : menu.getCount();
                String value = menu.getCode();
                redisTemplate.opsForZSet().add(key, value, count);
            });
            redisTemplate.expire(key, 31, TimeUnit.DAYS);
        }
    }

    private void assertCode(String code) {
        if (StringUtils.isEmpty(code)) {
            throw new CommonException("error.menuClick.code.empty");
        }
        validateCode(code);
    }

    private void validateCode(String code) {
        if (!Arrays.asList(ROOT_CODES).contains(code)) {
            throw new CommonException("error.menuClick.illegal.code");
        }
    }
}
