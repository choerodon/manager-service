package io.choerodon.manager.infra.common.utils.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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
     *
     * @param map map
     * @return map
     */
    @SuppressWarnings("unchecked")
    static Object mapParseRecursive(Map<String, Object> map) {
        Map<String, Object> res = new LinkedHashMap<>();
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            Object o = map.get(key);
            Object value = o;
            res.put(key, value);

        }
        return res;
    }
}
