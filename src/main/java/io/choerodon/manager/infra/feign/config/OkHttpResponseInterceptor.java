package io.choerodon.manager.infra.feign.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.exception.ExceptionResponse;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OkHttpResponseInterceptor implements Interceptor {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final Logger logger = LoggerFactory.getLogger(OkHttpResponseInterceptor.class);

    /**
     * 响应拦截器处理：异常状态码 及 CommonException 都打印错误日志，并进入fallback进行具体都处理
     *
     * @param chain
     * @return
     * @throws IOException
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        ResponseBody responseBodyCopy = response.peekBody(Long.MAX_VALUE);  // 避开调用 response.body().string() 因为该方法调用一次后response的流关闭
        String bodyStr = responseBodyCopy.string();
        // 如果请求异常（状态码异常）
        if (!response.isSuccessful()) {
            // 打印异常信息
            logger.error("An error occurred when feign request: {},error message:{}", request.url(), bodyStr);
            // 返回错误 response 以进入 fallback
            return response;
        }

        // 如果请求正常（状态码正常）但是 为 CommonException
        if (isCommonException(bodyStr)) {
            // 打印CommonException
            logger.warn("An CommonException occurred when feign request: {},error message:{}", request.url(), bodyStr);
            // 此处关闭流，并返回让response，CommonException情况进入fallback
            Util.closeQuietly(response);
            return response;
        } else {
            // 如果请求正常 且 返回值不是CommonException 则 直接返回 response
            return response;
        }
    }

    private boolean isCommonException(String responseBody) {
        try {
            objectMapper.readValue(responseBody, new TypeReference<ExceptionResponse>() {
            });
            return true;
        } catch (IOException e) {
            return false;
        }

    }
}
