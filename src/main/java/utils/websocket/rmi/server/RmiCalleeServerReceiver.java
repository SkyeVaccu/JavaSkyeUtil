package utils.websocket.rmi.server;

import utils.websocket.WebSocketPackage;
import utils.websocket.rmi.RmiCalleeReceiver;
import utils.websocket.server.WebSocketConnection;
import utils.websocket.server.WebSocketServerReceiver;

/**
 * @Description 服务端端方法被调者，当当前服务端作为被调者时，其会接收到调用方法的相关数据，应当将返回值返回 @Author Skye @Date 2022/11/28 16:04
 */
public class RmiCalleeServerReceiver extends WebSocketServerReceiver implements RmiCalleeReceiver {

    @Override
    public void bind(WebSocketConnection webSocketConnection) {}

    @Override
    public void receive(
            WebSocketConnection webSocketConnection, WebSocketPackage webSocketPackage) {}
}
