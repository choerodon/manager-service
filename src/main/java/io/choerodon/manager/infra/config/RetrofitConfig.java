package io.choerodon.manager.infra.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.manager.infra.retrofit.GoRegisterRetrofitClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.text.SimpleDateFormat;

/**
 * @author zongw.lee@gmail.com
 * @since  2019/11/1
 */
@Configuration
public class RetrofitConfig {

    @Value("${eureka.client.serviceUrl.defaultZone}")
    String goRegisterBaseUrl;

    @Bean("goRegisterRetrofitClient")
    public GoRegisterRetrofitClient goRegisterRetrofitClient() {
        Retrofit retrofit = getGoRegisterRetrofit();
        return retrofit.create(GoRegisterRetrofitClient.class);
    }

    private Retrofit getGoRegisterRetrofit() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getSerializationConfig().with(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return new Retrofit.Builder()
                .baseUrl(goRegisterBaseUrl)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();
    }
}
