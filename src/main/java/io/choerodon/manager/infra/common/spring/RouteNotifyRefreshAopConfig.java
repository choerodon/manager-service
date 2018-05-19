package io.choerodon.manager.infra.common.spring;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import io.choerodon.manager.infra.common.utils.GatewayProperties;
import io.choerodon.manager.infra.common.utils.RefreshUtil;

/**
 * 对@RouteNotifyRefresh注解的aop处理类
 *
 * @author wuguokai
 */
@Aspect
@Configuration
public class RouteNotifyRefreshAopConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigNotifyRefreshAopConfig.class);

    @Autowired
    private RefreshUtil refreshUtil;

    @Autowired
    private GatewayProperties gatewayProperties;

    @Pointcut("@annotation(io.choerodon.manager.infra.common.annotation.RouteNotifyRefresh)")
    public void executeService() {
        //for aop
    }

    /**
     * 在注解方法执行之后执行一下操作
     *
     * @param joinPoint 截点
     */
    @AfterReturning("executeService()")
    public void afterReturning(JoinPoint joinPoint) {
        try {
            for (int i = 0; i < gatewayProperties.getNames().length; i++) {
                refreshUtil.refresh(gatewayProperties.getNames()[i]);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
