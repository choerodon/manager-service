package io.choerodon.manager.infra.conventer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.IllegalArgumentException;
import io.choerodon.manager.api.dto.ConfigVO;
import io.choerodon.manager.infra.dto.ConfigDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * config转换工具类
 *
 * @author superlee
 * @since 2019-07-21
 */
public class ConfigConverter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ConfigConverter() {
        throw new IllegalStateException("Utility class");
    }

    public static ConfigVO dto2Vo(ConfigDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("error.config.null");
        }
        ConfigVO configVO = new ConfigVO();
        Map<String, Object> value = new HashMap<>();
        if (!StringUtils.isEmpty(value)) {
            try {
                value = MAPPER.readValue(dto.getValue(), Map.class);
            } catch (IOException e) {
                throw new CommonException("error.config.parse.dto.to.vo");
            }
        }
        BeanUtils.copyProperties(dto, configVO);
        configVO.setValue(value);
        return configVO;
    }

    public static List<ConfigVO> dto2Vo(List<ConfigDTO> dtoList) {
        List<ConfigVO> configs = new ArrayList<>();
        if (!ObjectUtils.isEmpty(dtoList)) {
            dtoList.forEach(dto -> configs.add(dto2Vo(dto)));
        }
        return configs;
    }

    public static PageInfo<ConfigVO> dto2Vo(PageInfo<ConfigDTO> pageInfo) {
        try (Page<ConfigVO> page = new Page<>(pageInfo.getPageNum(), pageInfo.getPageSize())) {
            List<ConfigDTO> dtoList = pageInfo.getList();
            page.setTotal(dtoList.size());
            List<ConfigVO> result = dto2Vo(dtoList);
            page.addAll(result);
            return page.toPageInfo();
        }
    }

    public static ConfigDTO vo2Dto(ConfigVO configVO) {
        ConfigDTO dto = new ConfigDTO();
        BeanUtils.copyProperties(configVO, dto);
        Map<String, Object> map = configVO.getValue();
        if (map != null) {
            try {
                dto.setValue(MAPPER.writeValueAsString(map));
            } catch (JsonProcessingException e) {
                throw new CommonException("error.config.parser");
            }
        }
        return dto;
    }
}
