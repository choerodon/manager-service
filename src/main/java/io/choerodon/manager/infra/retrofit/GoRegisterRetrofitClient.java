package io.choerodon.manager.infra.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * @author zongw.lee@gmail.com
 * @since 2019/11/1
 */
public interface GoRegisterRetrofitClient {

    @GET("apps")
    Call<ResponseBody> listApps();

    @POST("apps/{app-name}")
    Call<ResponseBody> createOrUpdateApp(@Path("app-name") String appName);

    @DELETE("apps/{app-name}/{instance-id}")
    Call<ResponseBody> deleteApp(@Path("app-name") String appName, @Path("instance-id") String instanceId);

}
