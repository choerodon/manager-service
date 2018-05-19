package io.choerodon.manager.infra.common.utils.format.builder;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.core.exception.CommonException;

/**
 * 请注意：该构造器并不会将kv形式配置项转为具有嵌套层次的json，而是直接转为扁平化的json
 *
 * @author wuguokai
 */
public class JsonBuilder implements Builder {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String build(Map<String, Object> kv) {
        try {
            return MAPPER.writeValueAsString(kv);
        } catch (JsonProcessingException e) {
            throw new CommonException("error.config.parser");
        }
    }
}
