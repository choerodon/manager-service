package io.choerodon.manager.infra.common.annotation;

import java.lang.annotation.*;

/**
 * 用于通知路由刷新
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigNotifyRefresh {
}
