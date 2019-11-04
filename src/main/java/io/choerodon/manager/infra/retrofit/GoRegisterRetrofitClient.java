package io.choerodon.manager.infra.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author zongw.lee@gmail.com
 * @since 2019/11/1
 */
public interface GoRegisterRetrofitClient {

    @GET("apps")
    Call<ResponseBody> getApp(@Path("app_name") String appName);

    @POST("apps/{app-name}")
    Call<ResponseBody> createOrUpdateApp(@Path("app_name") String appName, @Query("remote_token") String remoteToken);

}
