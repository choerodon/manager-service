package io.choerodon.manager.infra.feign.config;

import feign.Feign;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Feign.class)
@AutoConfigureBefore(FeignAutoConfiguration.class)
public class FeignChoerodonConfiguration {


    private OkHttpResponseInterceptor okHttpResponseInterceptor = new OkHttpResponseInterceptor();

    @Bean
    public okhttp3.OkHttpClient okHttpClient() {
        return new okhttp3.OkHttpClient.Builder() //创建具有默认设置的共享实例
                .addInterceptor(okHttpResponseInterceptor)
                .build();
    }
}
