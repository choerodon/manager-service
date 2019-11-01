package io.choerodon.manager.infra.utils;

public final class RegularExpression {

    private RegularExpression() {
    }

    /**
     * 字符串中只能由字母（大小写）、数字、"-"、"_"、"."、空格构成
     * 长度为1-30
     */
    public static final String ALPHANUMERIC_AND_SPACE_SYMBOLS = "^[a-zA-Z0-9-_\\.\\s]{1,30}$";

    /**
     * 路由描述正则校验 任意字符 限制200长度
     */
    public static final String ROUTE_RULE_ALL_SYMBOLS_200 = "^.{0,200}$";
}
