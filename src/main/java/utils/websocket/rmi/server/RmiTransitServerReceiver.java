package utils.websocket.rmi.server;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import utils.SerializeUtil;
import utils.reflect.RemoteMethodRequestWrapper;
import utils.websocket.WebSocketController;
import utils.websocket.WebSocketPackage;
import utils.websocket.server.WebSocketConnection;
import utils.websocket.server.WebSocketServerReceiver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description Rmi的中转服务接收器，用于找到目标对应的WebSocketConnection,然后将请求进行转发 @Author Skye @Date 2022/11/29
 * 15:11
 */
@WebSocketController(loop = "RemoteCallMethod", subject = "RemoteCallMethod")
public class RmiTransitServerReceiver extends WebSocketServerReceiver {

    // webSocketConnection列表
    private final List<WebSocketConnection> webSocketConnectionList = new ArrayList<>();

    @Override
    public void bind(WebSocketConnection webSocketConnection) {
        // 将所有的连接都作为待调用对象，进行绑定
        this.webSocketConnectionList.add(webSocketConnection);
        webSocketConnection.bind(this);
    }

    @Override
    public void receive(
            WebSocketConnection webSocketConnection, WebSocketPackage webSocketPackage) {
        // 请求数据包
        if (webSocketPackage
                .getWebSocketPackageType()
                .equals(WebSocketPackage.WebSocketPackageType.REQUEST)) {
            // 将该数据包进行注册
            CallMethodTransit.submit(webSocketConnection, webSocketPackage);
            // 获得发送的请求包
            RemoteMethodRequestWrapper remoteMethodRequestWrapper =
                    SerializeUtil.convertMapToBeanByClass(
                            (Map<String, Object>) webSocketPackage.getContent(),
                            new TypeReference<>() {});
            // 从所有的连接中，寻找目标的
            String callTargetEndKey = remoteMethodRequestWrapper.getCallTargetEndKey();
            // 请求目标为空，则该请求包发给所有人
            if (StringUtils.isEmpty(callTargetEndKey)) {
                webSocketConnectionList.forEach(
                        perWebSocketConnection -> perWebSocketConnection.send(webSocketPackage));
            } else {
                // 遍历所有绑定的请求，找到目标
                for (WebSocketConnection perWebSocketConnection : webSocketConnectionList) {
                    String perCallTargetEndKey =
                            perWebSocketConnection.getOpenParamMap().get("callTargetEndKey");
                    // 该接口愿意接收所有的方法调用或者两者的key相等
                    if (StringUtils.isEmpty(perCallTargetEndKey)
                            || StringUtils.equals(callTargetEndKey, perCallTargetEndKey)) {
                        perWebSocketConnection.send(webSocketPackage);
                    }
                }
            }
        }
        // 响应数据包
        else {
            // 将响应数据包发回给调用方
            CallMethodTransit.handle(webSocketPackage);
        }
    }
}
