package utils.websocket.rmi.server;

import utils.websocket.WebSocketPackage;
import utils.websocket.rmi.RmiCallerReceiver;
import utils.websocket.server.WebSocketConnection;
import utils.websocket.server.WebSocketServerReceiver;

/** @Description 服务端方法调用者，当当前服务端作为调用者时，其会接收到调用方法的返回值 @Author Skye @Date 2022/11/28 16:04 */
public class RmiCallerServerReceiver extends WebSocketServerReceiver implements RmiCallerReceiver {

    @Override
    public void bind(WebSocketConnection webSocketConnection) {}

    @Override
    public void receive(
            WebSocketConnection webSocketConnection, WebSocketPackage webSocketPackage) {}
}
