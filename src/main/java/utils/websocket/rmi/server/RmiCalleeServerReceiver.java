package utils.websocket.rmi.server;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import utils.SerializeUtil;
import utils.reflect.RemoteMethodRequestWrapper;
import utils.reflect.RemoteMethodResponseWrapper;
import utils.websocket.WebSocketController;
import utils.websocket.WebSocketPackage;
import utils.websocket.rmi.RmiCalleeReceiver;
import utils.websocket.rmi.RmiUtil;
import utils.websocket.server.WebSocketConnection;
import utils.websocket.server.WebSocketServerReceiver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Description 服务端端方法被调者，当当前服务端作为被调者时，其会接收到调用方法的相关数据，应当将返回值返回 @Author Skye @Date 2022/11/28 16:04
 */
@WebSocketController(loop = "RemoteCallMethod", subject = "RemoteCallMethod")
@NoArgsConstructor
public class RmiCalleeServerReceiver extends WebSocketServerReceiver implements RmiCalleeReceiver {
    // 用于存储webSocketConnection的列表
    private final List<WebSocketConnection> webSocketConnectionList = new ArrayList<>();
    // 当前绑定的服务终端在rmi中所使用的key,如果该值为空，则表示对所有的远程方法调用都进行响应
    private String rmiCalleeEndKey;

    public RmiCalleeServerReceiver(String rmiCalleeEndKey) {
        this.rmiCalleeEndKey = rmiCalleeEndKey;
    }

    @Override
    public void bind(WebSocketConnection webSocketConnection) {
        // 将所有的连接都作为待调用对象，进行绑定
        this.webSocketConnectionList.add(webSocketConnection);
        webSocketConnection.bind(this);
    }

    @Override
    public void receive(
            WebSocketConnection webSocketConnection, WebSocketPackage webSocketPackage) {
        // 只会对请求包进行处理
        if (webSocketPackage
                .getWebSocketPackageType()
                .equals(WebSocketPackage.WebSocketPackageType.REQUEST)) {
            // 获得发送的请求包
            RemoteMethodRequestWrapper remoteMethodRequestWrapper =
                    SerializeUtil.convertMapToBeanByClass(
                            (Map<String, Object>) webSocketPackage.getContent(),
                            new TypeReference<>() {});
            // 获得远程方法调用的终端key
            String callTargetEndKey = remoteMethodRequestWrapper.getCallTargetEndKey();
            // 如果对方无要求或者己方无要求，或者两者相等，则表示响应
            if (StringUtils.isAnyEmpty(callTargetEndKey, rmiCalleeEndKey)
                    || StringUtils.equals(rmiCalleeEndKey, callTargetEndKey)) {
                // 调用目标方法
                Object result = RmiUtil.callRemoteMethodRequestWrapper(remoteMethodRequestWrapper);
                // 如果方法具有返回值，则进行返回
                if (!"void"
                        .equals(
                                remoteMethodRequestWrapper
                                        .getMethodDefinition()
                                        .getReturnClass())) {
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
                    webSocketConnection.send(responseWebSocketPackage);
                }
            }
        }
    }
}
