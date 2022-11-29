package utils.websocket.rmi.server;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.ObjectUtils;
import utils.SerializeUtil;
import utils.reflect.RemoteMethodRequestWrapper;
import utils.websocket.WebSocketPackage;
import utils.websocket.server.WebSocketConnection;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description Rmi方法调用中转站，当发出一个方法调用请求后，在服务器上应当对该连接进行存储，用于拿到返回值后进行转发 @Author Skye @Date 2022/11/29
 * 15:21
 */
public class CallMethodTransit {
    // 用于存储方法调用
    private static final Map<String, WebSocketConnection> CALL_METHOD_TRANSIT = new HashMap<>();

    /**
     * 如果方法具有返回值，则将其注册到map中去
     *
     * @param webSocketConnection 请求方的webSocket连接
     * @param webSocketPackage 发送的请求数据包
     */
    public static void submit(
            WebSocketConnection webSocketConnection, WebSocketPackage webSocketPackage) {
        // 获得发送的请求包
        RemoteMethodRequestWrapper remoteMethodRequestWrapper =
                SerializeUtil.convertMapToBeanByClass(
                        (Map<String, Object>) webSocketPackage.getContent(),
                        new TypeReference<>() {});
        // 返回值不是void，则进行注册
        if (!"void".equals(remoteMethodRequestWrapper.getMethodDefinition().getReturnClass())) {
            CALL_METHOD_TRANSIT.put(webSocketPackage.getId(), webSocketConnection);
        }
    }

    /**
     * 已经拿到返回值，进行处理，将返回值发回给调用方
     *
     * @param webSocketPackage 响应的数据包
     */
    public static void handle(WebSocketPackage webSocketPackage) {
        WebSocketConnection webSocketConnection =
                CALL_METHOD_TRANSIT.get(webSocketPackage.getResponseId());
        if (ObjectUtils.isNotEmpty(webSocketConnection)) {
            // 将结果发回给调用方
            webSocketConnection.send(webSocketPackage);
            // 移除当前的待处理任务
            CALL_METHOD_TRANSIT.remove(webSocketPackage.getResponseId());
        }
    }
}
