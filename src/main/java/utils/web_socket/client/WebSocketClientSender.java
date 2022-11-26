package utils.web_socket.client;

import utils.web_socket.WebSocketPackage;

/**
 * @Description WebSocketClient 发送器
 * @Author Skye
 * @Date 2022/11/26 16:08
 * 继承该接口后，需要手动通过WebSocketClient的register方法，将其注册到服务器中，同时需要在子类上使用@WebSocketController去指定对应的回路
 * 和主题。当register时，就会调用bind方法注入对应client对象
 * <p>
 * 由于操作器与WebClient之间的为多对一的关系，因此并不需要在操作器上做连接的区分，相比较Server也就少了WebSocketConnection对象，由用户自己
 * 调用send方法完成数据包的发送
 */
public abstract class WebSocketClientSender extends WebSocketClientOperator {

    /**
     * 发送数据包
     *
     * @param webSocketPackage 数据包对象
     */
    public abstract void send(WebSocketPackage webSocketPackage);
}
