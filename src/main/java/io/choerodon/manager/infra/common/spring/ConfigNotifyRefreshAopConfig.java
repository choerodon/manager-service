package io.choerodon.manager.infra.common.spring;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import io.choerodon.manager.infra.common.utils.RefreshUtil;
import io.choerodon.manager.infra.dataobject.ServiceConfigDO;
import io.choerodon.manager.infra.dataobject.ServiceDO;
import io.choerodon.manager.infra.mapper.ServiceConfigMapper;
import io.choerodon.manager.infra.mapper.ServiceMapper;

/**
 * 对@ConfigNotifyRefresh注解的aop处理类
 *
 * @author wuguokai
 */
@Aspect
@Configuration
public class ConfigNotifyRefreshAopConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigNotifyRefreshAopConfig.class);

    @Autowired
    private ServiceMapper serviceMapper;
    @Autowired
    private ServiceConfigMapper serviceConfigMapper;
    @Autowired
    private RefreshUtil refreshUtil;

    @Pointcut("@annotation(io.choerodon.manager.infra.common.annotation.ConfigNotifyRefresh)")
    public void executeService() {
        // for aop
    }

    /**
     * 在注解方法执行之后执行一下操作
     *
     * @param joinPoint 截点
     */
    @AfterReturning("executeService()")
    public void afterReturning(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            Long configId = (Long) args[0];
            if (configId != null) {
                ServiceConfigDO serviceConfigDO = serviceConfigMapper.selectByPrimaryKey(configId);
                ServiceDO serviceDO = serviceMapper.selectByPrimaryKey(serviceConfigDO.getServiceId());
                refreshUtil.refresh(serviceDO.getName());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
