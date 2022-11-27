package utils.web_socket.client;

import utils.web_socket.WebSocketOperator;

/**
 * @Description WebSocketClient操作器 @Author Skye @Date 2022/11/26 9:51
 */
public abstract class WebSocketClientOperator implements WebSocketOperator {
    /**
     * 绑定WebSocketClient
     *
     * @param webSocketClient WebSocketClient
     */
    public abstract void bind(WebSocketClient webSocketClient);
}
