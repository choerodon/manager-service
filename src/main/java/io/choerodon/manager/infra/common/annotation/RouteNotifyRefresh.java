package io.choerodon.manager.infra.common.annotation;

import java.lang.annotation.*;

/**
 * 用于通知配置更新，使用该注解的方法第一个参数必须是configId
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RouteNotifyRefresh {
}
