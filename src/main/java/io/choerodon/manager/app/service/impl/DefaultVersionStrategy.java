package io.choerodon.manager.app.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import io.choerodon.manager.app.service.VersionStrategy;
import io.choerodon.manager.infra.common.utils.VersionUtil;

/**
 * 实现类
 *
 * @author zhipeng.zuo
 * @author wuguokai
 */
@Component
public class DefaultVersionStrategy implements VersionStrategy {

    @Override
    public int compareVersion(String v1, String v2) {
        if (v1.equals(v2)) {
            return 0;
        }
        if (VersionUtil.NULL_VERSION.equals(v1) || StringUtils.isEmpty(v1)) {
            return -1;
        }
        if (VersionUtil.NULL_VERSION.equals(v2) || StringUtils.isEmpty(v2)) {
            return 1;
        }
        char[] chars1 = v1.toCharArray();
        char[] chars2 = v2.toCharArray();
        if (chars1.length <= chars2.length) {
            return compareCharArray(chars1, chars2);
        } else {
            return 0 - compareCharArray(chars1, chars2);
        }
    }

    private int compareCharArray(char[] chars1, char[] chars2) {
        for (int i = 0; i < chars1.length; i++) {
            char ch1 = chars1[i];
            char ch2 = chars2[i];
            if (ch1 > ch2) {
                return 1;
            } else if (ch1 < ch2) {
                return -1;
            }
        }
        return 0;
    }
}
