package io.choerodon.manager.infra.common.utils.format.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * propertier配置文件的解析器
 *
 * @author wuguokai
 */
public class PropertiesParser implements Parser {

    /**
     * 把peoperties文件内容解析成map键值对集合
     *
     * @param content 配置内容
     * @return map
     * @throws IOException 文件读写异常
     */
    @Override
    public Map<String, Object> parse(String content) throws IOException {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(content.getBytes()));
        Set<Object> keys = properties.keySet();
        Map<String, Object> map = new LinkedHashMap<>();
        for (Object k : keys) {
            map.put((String) k, properties.get(k));
        }
        return map;
    }
}
