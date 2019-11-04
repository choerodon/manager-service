package io.choerodon.manager.infra.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ExceptionResponse;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/8/29
 */
public class RetrofitCallExceptionParse {

    private static final Logger logger = LoggerFactory.getLogger(RetrofitCallExceptionParse.class);
    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd hh:mm:ss")
            .enableComplexMapKeySerialization().create();

    private RetrofitCallExceptionParse() {
    }

    /**
     * 执行请求并返回执行对象
     *
     * @param call             需要执行的call
     * @param exceptionMessage 报错信息
     * @param clazz            期望的返回类型
     * @return T
     */
    public static <T> T executeCall(Call<ResponseBody> call, String exceptionMessage, Class<T> clazz) {
        return gson.fromJson(parseException(call, exceptionMessage), clazz);
    }

    /**
     * 执行请求并返回执行对象
     *
     * @param call             需要执行的call
     * @param exceptionMessage 报错信息
     * @param clazz            期望的返回类型，数组格式
     * @return List<T>
     */
    public static <T> List<T> executeCallForList(Call<ResponseBody> call, String exceptionMessage, Class<T> clazz) {
        Type type = new ParameterizedListTypeImpl(clazz);
        return gson.fromJson(parseException(call, exceptionMessage), type);
    }

    /**
     * 执行请求并返回执行对象
     *
     * @param call             需要执行的call
     * @param exceptionMessage 报错信息
     * @param clazzKey         期望返回Map的key类型
     * @param clazzValue       期望返回Map的value类型
     * @return Map<K                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               ,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               T>
     */
    public static <K, T> Map<K, T> executeCallForMap(Call<ResponseBody> call, String exceptionMessage, Class<K> clazzKey, Class<T> clazzValue) {
        Map map = gson.fromJson(parseException(call, exceptionMessage), Map.class);
        Map<K, T> resMap = new HashMap<>();
        map.forEach((k, v) ->
                resMap.put(getLoadedInstanceWithGson(k, clazzKey), getLoadedInstanceWithGson(v, clazzValue))
        );
        return resMap;
    }

    private static <T> T getLoadedInstanceWithGson(Object object, Class<T> clazz) {
        return gson.fromJson(gson.toJson(object), clazz);
    }

    private static String parseException(Call<ResponseBody> call, String exceptionMessage) {
        String bodyStr;
        try {
            Response<ResponseBody> execute = call.execute();
            if (execute == null) {
                logger.info("::Retrofit::response is null");
                throw new CommonException("error.retrofit.execute.response.is.empty");
            }
            if (!execute.isSuccessful()) {
                logger.info("::Retrofit::unsuccessful");
                Optional.ofNullable(execute.errorBody()).ifPresent(v -> {
                    try {
                        logger.info("::Retrofit::error body:{}", v.string());
                    } catch (IOException e) {
                        throw new CommonException("error.retrofit.execute.is.unsuccessful", e);
                    }
                });
                throw new CommonException("error.retrofit.execute.is.unsuccessful");
            }
            if (ObjectUtils.isEmpty(execute.body())) {
                logger.info("::Retrofit::response body is null");
                throw new CommonException("error.retrofit.execute.response.body.is.empty");
            }
            bodyStr = execute.body().string();
        } catch (IOException e) {
            logger.info("::Retrofit::An exception occurred during execution:{}", e);
            throw new CommonException("error.retrofit.execute", e);
        }
        parseCommonException(bodyStr, exceptionMessage);
        return bodyStr;
    }

    /**
     * 解析响应是否是CommonException
     */
    private static void parseCommonException(String responseStr, String exceptionMessage) {
        try {
            ExceptionResponse e = gson.fromJson(responseStr, ExceptionResponse.class);
            if (e.getFailed() != null && Boolean.TRUE.equals(e.getFailed())) {
                logger.info("::Retrofit::The response is CommonException，code:{},message:{}", e.getCode(), e.getMessage());
                throw new CommonException(exceptionMessage);
            }
        } catch (JsonParseException e) {
            logger.info("::Retrofit::The response can not be parsed into CommonException,continue to be parsed into the given Class, " +
                    "Parse exception message:{}", e.getMessage());
        }
    }

    private static class ParameterizedListTypeImpl implements ParameterizedType {
        Class clazz;

        private ParameterizedListTypeImpl(Class clz) {
            clazz = clz;
        }

        @Override
        public Type[] getActualTypeArguments() {
            //返回实际类型组成的数据
            return new Type[]{clazz};
        }

        @Override
        public Type getRawType() {
            //返回原生类型
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}
