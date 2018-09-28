package io.choerodon.manager.infra.common.utils.config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 递归解析map数据
 *
 * @author wuguokai
 */
class TreeProcess {

    private TreeProcess() {
    }

    /**
     * 递归解析map形式集合
     */
    static Map<String, Object> mapParseRecursive(final Map<String, Object> parse) {
        final Map<String, Object> returnMap = new LinkedHashMap<>();
        parse(returnMap, parse, null);
        return returnMap;
    }


    @SuppressWarnings("unchecked")
    static void parse(final Map<String, Object> returnMap, final Map<String, Object> parse, String prefix) {
        parse.forEach((k, v) -> {
            if (v instanceof Map) {
                if (prefix == null) {
                    parse(returnMap, (Map<String, Object>) v, k + ".");
                } else {
                    parse(returnMap, (Map<String, Object>) v, prefix + k + ".");
                }
            } else {
                if (prefix == null) {
                    returnMap.put(k, v);
                } else {
                    returnMap.put(prefix + k, v);
                }
            }
        });
    }
}
