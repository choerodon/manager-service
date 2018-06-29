package io.choerodon.manager.infra.common.utils.config;

import java.util.LinkedHashMap;
import java.util.List;
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
            if (o instanceof Map) {
                Map tmpMap = (Map) o;
                Map kvMap = (Map) mapParseRecursive(tmpMap);
                Set<String> kvKeySet = kvMap.keySet();
                for (String kvKey : kvKeySet) {
                    res.put(key + "." + kvKey, kvMap.get(kvKey));
                }
            } else if (o instanceof List) {
                Map tmpMap = listParseRecursive((List) o);
                Set<String> tmpKeySet = tmpMap.keySet();
                for (String tmpKey : tmpKeySet) {
                    res.put(key + tmpKey, tmpMap.get(tmpKey).toString());
                }
            } else {
                Object value = o;
                res.put(key, value);
            }
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> listParseRecursive(List list) {
        Map<String, Object> res = new LinkedHashMap<>();

        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            if (o instanceof Map) {
                Map tmpMap = (Map) mapParseRecursive((Map) o);
                Set<String> keySet = tmpMap.keySet();
                for (String kvKey : keySet) {
                    res.put("[" + i + "]." + kvKey, tmpMap.get(kvKey).toString());
                }
            } else if (o instanceof List) {
                Map tmpMap = listParseRecursive((List) o);
                Set<String> keySet = tmpMap.keySet();
                for (String key : keySet) {
                    res.put("[" + i + "]" + key, tmpMap.get(key).toString());
                }
            } else if (o != null) {
                res.put("[" + i + "]", o.toString());
            }
        }
        return res;
    }
}
