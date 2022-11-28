package utils.websocket.rmi.client;

import utils.websocket.WebSocketPackage;
import utils.websocket.client.WebSocketClient;
import utils.websocket.client.WebSocketClientReceiver;
import utils.websocket.rmi.RmiCallerReceiver;

/** @Description 客户端方法调用者，当当前客户端作为调用者时，其会接收到调用方法的返回值 @Author Skye @Date 2022/11/28 16:05 */
public class RmiCallerClientReceiver extends WebSocketClientReceiver implements RmiCallerReceiver {

    @Override
    public void bind(WebSocketClient webSocketClient) {}

    @Override
    public void receive(WebSocketPackage webSocketPackage) {}
}
