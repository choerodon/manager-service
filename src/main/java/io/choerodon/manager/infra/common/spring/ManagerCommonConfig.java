package io.choerodon.manager.infra.common.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class ManagerCommonConfig {

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
