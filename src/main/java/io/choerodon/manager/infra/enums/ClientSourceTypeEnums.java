package io.choerodon.manager.infra.enums;

/**
 * @author wanghao
 * @Date 2019/11/4 14:56
 */
public enum ClientSourceTypeEnums {
    POD("pod"),

    CUSTOM("custom");

    private final String value;

    ClientSourceTypeEnums(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
