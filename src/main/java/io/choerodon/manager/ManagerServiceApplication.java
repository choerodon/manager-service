package io.choerodon.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import io.choerodon.manager.infra.common.utils.GatewayProperties;
import io.choerodon.resource.annoation.EnableChoerodonResourceServer;

@EnableChoerodonResourceServer
@EnableEurekaClient
@EnableFeignClients
@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(GatewayProperties.class)
public class ManagerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManagerServiceApplication.class, args);
    }

    @Bean
    public Docket customImplementation() {
        ApiInfo apiInfo = ApiInfo.DEFAULT;
        apiInfo = new ApiInfo(apiInfo.getTitle(), "my descript", apiInfo.getVersion(),
                apiInfo.getTermsOfServiceUrl(), apiInfo.getContact(), apiInfo.getLicense(), apiInfo.getLicenseUrl());
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("test")
                .apiInfo(apiInfo);
    }
}
