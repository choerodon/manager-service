package io.choerodon.manager.infra.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import io.choerodon.manager.infra.retrofit.GoRegisterRetrofitClient;

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
        Gson gson = new GsonBuilder()
                //配置Gson
                .setDateFormat("yyyy-MM-dd hh:mm:ss")
                .create();
        return new Retrofit.Builder()
                .baseUrl(goRegisterBaseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}
