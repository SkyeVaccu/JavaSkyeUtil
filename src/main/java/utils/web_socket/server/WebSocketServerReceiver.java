package utils.web_socket.server;

import utils.web_socket.WebSocketPackage;

/**
 * @Description websocket Server 接收者
 * @Author Skye
 * @Date 2022/11/26 9:31
 * 继承该接口后，需要手动通过WebSocketServer的register方法，将其注册到服务器中，同时需要在子类上使用@WebSocketController去指定对应的回路
 * 和主题，这样才会在每次有客户端连接的时候，调用其Bind方法，将其与目标的WebSocketConnection进行连接，在接收到消息时会自动调用receive方法
 */
public abstract class WebSocketServerReceiver extends WebSocketServerOperator {

    /**
     * 接收对应的数据包，该接口会被自动调用，操作器对应多个连接，需要告知用户数据包的来源
     *
     * @param webSocketConnection 收到数据包的连接
     * @param webSocketPackage    数据包
     */
    public abstract void receive(WebSocketConnection webSocketConnection, WebSocketPackage webSocketPackage);
}
