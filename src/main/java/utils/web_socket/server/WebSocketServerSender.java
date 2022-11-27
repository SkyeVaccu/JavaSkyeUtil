package utils.web_socket.server;

import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import utils.web_socket.WebSocketPackage;

/**
 * @Description websocket Server 发送者 @Author Skye @Date 2022/11/26 9:32
 * 继承该接口后，需要手动通过WebSocketServer的register方法，将其注册到服务器中，同时需要在子类上使用@WebSocketController去指定对应的回路
 * 和主题，这样才会在每次有客户端连接的时候，调用其Bind方法，将其与目标的WebSocketConnection进行连接
 */
public abstract class WebSocketServerSender extends WebSocketServerOperator {

    /**
     * 发送对应的数据包给指定的连接，这个连接应当由用户决定发送给谁，因为操作器对应着多个连接
     *
     * @param webSocketConnection 发送的目标websocket连接
     * @param webSocketPackage 需要发送的数据包
     */
    public void send(WebSocketConnection webSocketConnection, WebSocketPackage webSocketPackage) {
        // 如果连接未关闭，则发送数据包，如果关闭，则抛出异常
        if (!webSocketConnection.getWebSocket().isClosed()) {
            webSocketConnection.send(webSocketPackage);
        } else {
            throw SkyeUtilsExceptionFactory.createException(
                    SkyeUtilsExceptionType.WebSocketConnectionCloseException);
        }
    }
}
