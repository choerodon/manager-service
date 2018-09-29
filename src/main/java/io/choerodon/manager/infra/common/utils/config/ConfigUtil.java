package io.choerodon.manager.infra.common.utils.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.choerodon.manager.app.service.impl.InstanceServiceImpl;
import io.codearte.props2yaml.Props2YAML;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.choerodon.manager.app.service.impl.ConfigServiceImpl.CONFIG_TYPE_PROPERTIES;
import static io.choerodon.manager.app.service.impl.ConfigServiceImpl.CONFIG_TYPE_YAML;

public class ConfigUtil {

    private static final ObjectMapper YAM_MAPPER = new ObjectMapper(new YAMLFactory());

    private ConfigUtil() {
    }

    public static String convertDataMapToYaml(final Map<String, InstanceServiceImpl.Data> dataMap) {
        StringBuilder res = new StringBuilder();
        for (Map.Entry<String, InstanceServiceImpl.Data> entry : dataMap.entrySet()) {
            res.append(entry.getKey());
            res.append("=");
            res.append(entry.getValue().getValue());
            res.append("\n");
        }
        return Props2YAML.fromContent(res.toString())
                .convert();
    }

    public static String convertMapToText(final Map<String, Object> configMap, final String type) {
        ConfigFileFormat configFileFormat = ConfigFileFormat.fromString(type);
        Builder builder = BuilderFactory.getBuilder(configFileFormat);
        return builder.build(configMap);
    }

    public static Map<String, Object> convertTextToMap(final String type, final String configText) throws IOException {
        switch (type) {
            case CONFIG_TYPE_YAML:
                return parseYaml(configText);
            case CONFIG_TYPE_PROPERTIES:
                return parseProperties(configText);
            default:
                return parseProperties(configText);
        }
    }

    public static String convertJsonToYaml(String jsonString) throws IOException {
        JsonNode jsonNodeTree = new ObjectMapper().readTree(jsonString);
        return new YAMLMapper().writeValueAsString(jsonNodeTree);
    }

    private static Map<String, Object> parseProperties(String content) throws IOException {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(content.getBytes()));
        Set<Object> keys = properties.keySet();
        Map<String, Object> map = new LinkedHashMap<>();
        for (Object k : keys) {
            map.put((String) k, properties.get(k));
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> parseYaml(String content) throws IOException {
        LinkedHashMap<String, Object> root = YAM_MAPPER.readValue(content, LinkedHashMap.class);
        return TreeProcess.mapParseRecursive(root);
    }

    public static int appearNumber(String srcText, String findText) {
        int count = 0;
        Pattern p = Pattern.compile(findText);
        Matcher m = p.matcher(srcText);
        while (m.find()) {
            count++;
        }
        return count;
    }

}
