package io.choerodon.manager.infra.retrofit;

import io.choerodon.manager.api.dto.HostWarpPortDTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

/**
 * @author zongw.lee@gmail.com
 * @since 2019/11/1
 */
public interface GoRegisterRetrofitClient {

    @GET("apps")
    Call<ResponseBody> listApps();

    @POST("apps/{app-name}")
    Call<ResponseBody> createHost(@Path("app-name") String appName, @Body HostWarpPortDTO host);

    @DELETE("apps/{app-name}/{instance-id}")
    Call<ResponseBody> deleteHost(@Path("app-name") String appName, @Path("instance-id") String instanceId);

    @PUT("apps/metadata")
    Call<ResponseBody> updateApps(@Body Map<String, Map<String, String>> updateData);

}
