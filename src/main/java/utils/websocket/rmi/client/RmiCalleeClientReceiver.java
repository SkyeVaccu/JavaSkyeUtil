package utils.websocket.rmi.client;

import com.fasterxml.jackson.core.type.TypeReference;
import utils.SerializeUtil;
import utils.reflect.RemoteMethodRequestWrapper;
import utils.reflect.RemoteMethodResponseWrapper;
import utils.websocket.WebSocketController;
import utils.websocket.WebSocketPackage;
import utils.websocket.client.WebSocketClient;
import utils.websocket.client.WebSocketClientReceiver;
import utils.websocket.rmi.RmiCalleeReceiver;
import utils.websocket.rmi.RmiUtil;

import java.util.Map;
import java.util.UUID;

/**
 * @Description 客户端方法被调者，当当前客户端作为被调者时，其会接收到调用方法的相关数据，应当将返回值返回 @Author Skye @Date 2022/11/28 16:04
 */
@WebSocketController(loop = "RemoteCallMethod", subject = "RemoteCallMethod")
public class RmiCalleeClientReceiver extends WebSocketClientReceiver implements RmiCalleeReceiver {
    // 当前绑定的webSocketClient对象
    private WebSocketClient webSocketClient;

    @Override
    public void bind(WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    @Override
    public void receive(WebSocketPackage webSocketPackage) {
        // 获得发送的请求包
        RemoteMethodRequestWrapper remoteMethodRequestWrapper =
                SerializeUtil.convertMapToBeanByClass(
                        (Map<String, Object>) webSocketPackage.getContent(),
                        new TypeReference<>() {});
        // 调用目标方法
        Object result = RmiUtil.callRemoteMethodRequestWrapper(remoteMethodRequestWrapper);
        // 如果方法具有返回值，则进行返回
        if (!remoteMethodRequestWrapper.getMethodDefinition().getReturnClass().equals("void")) {
            // 生成返回的请求包
            WebSocketPackage responseWebSocketPackage =
                    new WebSocketPackage()
                            // 设置id
                            .setId(UUID.randomUUID().toString())
                            // 设置其响应的包
                            .setResponseId(webSocketPackage.getId())
                            // 设置内容
                            .setContent(new RemoteMethodResponseWrapper(result))
                            // 设置发送时间
                            .setTime(System.currentTimeMillis())
                            // 设置回路为远程方法调用
                            .setLoop("RemoteCallMethod")
                            // 设置主题为远程方法调用
                            .setSubject("RemoteCallMethod")
                            // 设置数据包的类型为请求
                            .setWebSocketPackageType(
                                    WebSocketPackage.WebSocketPackageType.RESPONSE);
            // 发送该返回值
            webSocketClient.send(responseWebSocketPackage);
        }
    }
}
