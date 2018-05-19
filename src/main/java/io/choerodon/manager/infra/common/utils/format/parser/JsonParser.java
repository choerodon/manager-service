package io.choerodon.manager.infra.common.utils.format.parser;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.choerodon.manager.infra.common.utils.format.parser.support.TreeProcess;

/**
 * json配置文件的解析器
 *
 * @author wuguokai
 */
public class JsonParser implements Parser {
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> parse(String content) throws IOException {
        LinkedHashMap<String, Object> root = MAPPER.readValue(content, LinkedHashMap.class);
        return (LinkedHashMap) TreeProcess.mapParseRecursive(root);
    }
}
