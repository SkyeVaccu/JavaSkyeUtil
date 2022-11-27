package utils.web_socket.server;

import utils.web_socket.WebSocketOperator;

/**
 * @Description WebSocket操作器 @Author Skye @Date 2022/11/26 9:51
 */
public abstract class WebSocketServerOperator implements WebSocketOperator {

    /**
     * 绑定WebSocketConnection连接，与client只需要与Client绑定不同，服务端需要与多个WebSocketConnection绑定
     * 而且WebSocketConnection是在客户端连接进来的时候绑定的，因此该方法会在每次有客户端连接的时候被调用，
     * 用户需要自己判断是否需要与当前的这个连接绑定，同时操作器应当存储多个连接，因为WebSocketConnection与操作器之间是多对一的关系
     *
     * @param webSocketConnection WebSocketConnection连接对象
     */
    public abstract void bind(WebSocketConnection webSocketConnection);
}
