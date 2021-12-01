package Utils;

import Log.SkyeLogger;
import SkyeUtilsException.SkyeUtilsExceptionType;
import SkyeUtilsException.SkyeUtilsExceptionFacoty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class HttpUtil {

    private static HttpClient client=HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NEVER)
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    /**
     * 检查路径和参数
     * @param requestPath 请求的路径
     * @param params 请求的参数
     * @return
     */
    private static boolean checkPathAndParams(String requestPath,Map<String,Object> params){
        if(StringUtils.isEmpty(requestPath))
            return false;
        for(Iterator<Entry<String,Object>> iterator = params.entrySet().iterator(); iterator.hasNext();){
            Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            if(StringUtils.isEmpty(key))
                return false;
            Object value = entry.getValue();
            //判断是否是基本数据类型,以及对应的值是否为空
            if((!value.getClass().isPrimitive())||(null==value||"".equals(value)))
                return false;
        }
        return true;
    }


    /**
     * 用于将对应的参数添加到请求的路径中去
     * @param requestPath 请求的路径
     * @param params 请求的参数，键和值均不能为空
     * @return 返回拼接后的字符串
     * @throws Exception
     */
    private static String appendParamsOnPath(String requestPath,Map<String,Object> params) throws Exception {
        int cnt = 0;
        StringBuffer requestPathStrBuff = new StringBuffer(requestPath);
        for (Iterator<Entry<String,Object>> iterator = params.entrySet().iterator(); iterator.hasNext();) {
            Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            //获得对应的值
            Object value = entry.getValue();
            //判断是否是基本数据类型,以及对应的值是否为空
            if (0 == cnt)
                requestPathStrBuff.append("?");
            else
                requestPathStrBuff.append("&");
            requestPathStrBuff.append(key + "=" + value);
            cnt++;
        }
        return requestPathStrBuff.toString();
    }


    /**
     * 发送一个Get请求
     * @param requestPath 请求的路径
     * @param params 对应的参数
     * @return 返回一个Response对象
     * @throws Exception
     */
    public static HttpResponse getRequest(String requestPath, Map<String,Object> params) throws Exception {
        //检查路径和参数
        if(!checkPathAndParams(requestPath, params))
            throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.RequestPathAndParamsErrorException);

        HttpRequest getRequest = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .timeout(Duration.ofSeconds(5))
                .uri(URI.create(appendParamsOnPath(requestPath, params)))
                .build();
        //发送请求
        return client.send(getRequest, HttpResponse.BodyHandlers.ofString());
    }


    /**
     * 发送一个Post请求
     * @param requestPath 请求的路径
     * @param params 需要传递的参数
     * @return 返回一个HttpResponse对象
     * @throws Exception
     */
    public static HttpResponse postRequest(String requestPath,Map<String,Object> params) throws Exception {
        //只检查路径
        if(StringUtils.isEmpty(requestPath))
            throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.RequestPathAndParamsErrorException);
        HttpRequest postRequest = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .method("POST", HttpRequest.BodyPublishers.ofString(SeriliazeUtil.convertObjectToJson(params)))
                .timeout(Duration.ofSeconds(5))
                .uri(URI.create(requestPath))
                .build();
        return client.send(postRequest,HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 发送一个Put请求
     * @param requestPath 请求的路径
     * @param params 需要传递的参数
     * @return 返回一个HttpResponse对象
     * @throws Exception
     */
    public static HttpResponse putRequest(String requestPath,Map<String,Object> params) throws Exception {
        return postRequest(requestPath, params);
    }

    /**
     * 发送一个Delete请求
     * @param requestPath 请求的路径
     * @param params 需要传递的参数
     * @return 返回一个HttpResponse对象
     * @throws Exception
     */
    public static HttpResponse deleteRequest(String requestPath,Map<String,Object> params) throws Exception {
        return postRequest(requestPath, params);
    }
}
