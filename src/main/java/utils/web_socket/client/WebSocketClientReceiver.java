package utils.web_socket.client;

import utils.web_socket.WebSocketPackage;

/**
 * @Description WebSocketClient 接收器 @Author Skye @Date 2022/11/26 16:08
 * 继承该接口后，需要手动通过WebSocketClient的register方法，将其注册到服务器中，同时需要在子类上使用@WebSocketController去指定对应的回路
 * 和主题。当register时，就会调用bind方法注入对应client对象，当接收到对应回路和主题的数据包时，也就会自动调用receive方法
 *
 * <p>由于操作器与WebClient之间的为多对一的关系，因此并不需要在操作器上做连接的区分，相比较Server也就少了WebSocketConnection对象
 */
public abstract class WebSocketClientReceiver extends WebSocketClientOperator {

    /**
     * 接收方法
     *
     * @param webSocketPackage 数据包
     */
    public abstract void receive(WebSocketPackage webSocketPackage);
}
