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
        parse(returnMap, parse);
        return returnMap;
    }


    @SuppressWarnings("unchecked")
    static void parse(final Map<String, Object> returnMap, final Map<String, Object> parse) {
        parse.forEach((k, v) -> {
            if (v instanceof Map) {
                parse(returnMap, (Map<String, Object>) v);
            } else {
                returnMap.put(k, v);
            }
        });
    }
}
