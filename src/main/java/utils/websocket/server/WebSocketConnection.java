package utils.websocket.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.java_websocket.WebSocket;
import utils.SerializeUtil;
import utils.websocket.WebSocketPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @Description WebSocket连接对象 @Author Skye @Date 2022/11/25 23:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class WebSocketConnection {
    // 连接打开时间
    private Long openTime;
    // 连接对象
    private WebSocket webSocket;
    // WebSocket打开时的参数列表
    private Map<String, String> openParamMap;
    // 连接更新时间
    private Long updateTime;
    // 连接关闭时间
    private Long closeTime;
    // 所有WebSocketOperator操作器
    private List<WebSocketServerOperator> webSocketServerOperatorList = new ArrayList<>();
    // 收到信息时的监听器
    private Consumer<String> messageListener;
    // 关闭时的监听器
    private Consumer<String> closeListener;
    // 发生错误时的监听器
    private Consumer<Exception> errorListener;

    /**
     * 将对应的消息包转换为json然后发送
     *
     * @param webSocketPackage 对应的消息包
     */
    public void send(WebSocketPackage webSocketPackage) {
        webSocket.send(SerializeUtil.convertObjectToJson(webSocketPackage));
    }

    /**
     * 添加对应的操作器
     *
     * @param webSocketServerOperator 操作器对象
     */
    public void bind(WebSocketServerOperator webSocketServerOperator) {
        webSocketServerOperatorList.add(webSocketServerOperator);
    }
}
