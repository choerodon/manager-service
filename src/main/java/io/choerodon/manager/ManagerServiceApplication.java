package io.choerodon.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.choerodon.eureka.event.EurekaEventHandler;
import io.choerodon.manager.infra.common.utils.GatewayProperties;
import io.choerodon.resource.annoation.EnableChoerodonResourceServer;

@EnableEurekaClient
@EnableFeignClients("io.choerodon")
@EnableScheduling
@EnableChoerodonResourceServer
@SpringBootApplication
@EnableConfigurationProperties(GatewayProperties.class)
public class ManagerServiceApplication {

    public static void main(String[] args) {
        EurekaEventHandler.getInstance().init();
        SpringApplication.run(ManagerServiceApplication.class, args);
    }
    
    

}
