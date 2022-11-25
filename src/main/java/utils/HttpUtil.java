package utils;

import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @Description http request对象
 * @Author Skye
 * @Date 2022/11/25 11:04
 */
public class HttpUtil {

    //超市时间
    public static final int TIME_OUT = 5;

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NEVER)
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    /**
     * 检查路径和参数
     *
     * @param requestPath 请求的路径
     * @param params      请求的参数
     */
    private static boolean checkPathAndParams(String requestPath, Map<String, Object> params) {
        if (StringUtils.isEmpty(requestPath)) {
            return false;
        }
        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            if (StringUtils.isEmpty(key)) {
                return false;
            }
            Object value = entry.getValue();
            // 判断是否是基本数据类型,以及对应的值是否为空
            if ((!value.getClass().isPrimitive()) || ObjectUtils.isEmpty(value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 用于将对应的参数添加到请求的路径中去
     *
     * @param requestPath 请求的路径
     * @param params      请求的参数，键和值均不能为空
     * @return 返回拼接后的字符串
     */
    private static String appendParamsOnPath(String requestPath, Map<String, Object> params) {
        int cnt = 0;
        StringBuilder requestPathStrBuilder = new StringBuilder(requestPath);
        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            // 获得对应的值
            Object value = entry.getValue();
            // 判断是否是基本数据类型,以及对应的值是否为空
            requestPathStrBuilder
                    .append(0 == cnt ? "?" : "&")
                    .append(key)
                    .append("=")
                    .append(value);
            cnt++;
        }
        return requestPathStrBuilder.toString();
    }

    /**
     * 发送一个Get请求
     *
     * @param requestPath 请求的路径
     * @param params      对应的参数
     * @return 返回一个Response对象
     * @throws Exception 请求错误异常
     */
    public static HttpResponse<String> getRequest(String requestPath, Map<String, Object> params)
            throws Exception {
        // 检查路径和参数
        if (!checkPathAndParams(requestPath, params)) {
            throw SkyeUtilsExceptionFactory.createException(
                    SkyeUtilsExceptionType.RequestPathAndParamsErrorException);
        }
        HttpRequest getRequest = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .timeout(Duration.ofSeconds(TIME_OUT))
                .uri(URI.create(appendParamsOnPath(requestPath, params)))
                .build();
        // 发送请求
        return CLIENT.send(getRequest, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 发送一个Post请求
     *
     * @param requestPath 请求的路径
     * @param params      需要传递的参数
     * @return 返回一个HttpResponse对象
     * @throws Exception 请求错误异常
     */
    public static HttpResponse<String> postRequest(String requestPath, Map<String, Object> params)
            throws Exception {
        // 只检查路径
        if (StringUtils.isEmpty(requestPath)) {
            throw SkyeUtilsExceptionFactory.createException(
                    SkyeUtilsExceptionType.RequestPathAndParamsErrorException);
        }
        HttpRequest postRequest = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .method("POST", HttpRequest.BodyPublishers.ofString(SerializeUtil.convertObjectToJson(params)))
                .timeout(Duration.ofSeconds(TIME_OUT))
                .uri(URI.create(requestPath))
                .build();
        return CLIENT.send(postRequest, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 发送一个Put请求
     *
     * @param requestPath 请求的路径
     * @param params      需要传递的参数
     * @return 返回一个HttpResponse对象
     * @throws Exception 请求错误异常
     */
    public static HttpResponse<String> putRequest(String requestPath, Map<String, Object> params)
            throws Exception {
        return postRequest(requestPath, params);
    }

    /**
     * 发送一个Delete请求
     *
     * @param requestPath 请求的路径
     * @param params      需要传递的参数
     * @return 返回一个HttpResponse对象
     * @throws Exception 请求错误异常
     */
    public static HttpResponse<String> deleteRequest(String requestPath, Map<String, Object> params)
            throws Exception {
        return postRequest(requestPath, params);
    }
}
