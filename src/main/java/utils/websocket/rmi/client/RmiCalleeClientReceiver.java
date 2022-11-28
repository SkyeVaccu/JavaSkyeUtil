package utils.websocket.rmi.client;

import utils.websocket.WebSocketPackage;
import utils.websocket.client.WebSocketClient;
import utils.websocket.client.WebSocketClientReceiver;
import utils.websocket.rmi.RmiCalleeReceiver;

/**
 * @Description 客户端方法被调者，当当前客户端作为被调者时，其会接收到调用方法的相关数据，应当将返回值返回 @Author Skye @Date 2022/11/28 16:04
 */
public class RmiCalleeClientReceiver extends WebSocketClientReceiver implements RmiCalleeReceiver {
    @Override
    public void bind(WebSocketClient webSocketClient) {}

    @Override
    public void receive(WebSocketPackage webSocketPackage) {}
}
