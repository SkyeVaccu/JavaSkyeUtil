package utils.web_socket.client;

import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import log.SkyeLogger;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import utils.SerializeUtil;
import utils.collection.Table;
import utils.web_socket.WebSocketPackage;
import utils.web_socket.WebSocketUtil;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @Description WebSocket客户端
 * @Author Skye
 * @Date 2022/11/26 15:50
 */
public abstract class WebSocketClient extends org.java_websocket.client.WebSocketClient {
    private static final Logger logger = SkyeLogger.getLogger();

    // websocket 接收者table，通过loop,subject确定接收器
    protected final Table<String, String, Set<WebSocketClientReceiver>> WEB_SOCKET_CLIENT_RECEIVER_LIST_TABLE = new Table<>();
    // websocket 发送者table，通过loop,subject确定发送器
    protected final Table<String, String, Set<WebSocketClientSender>> WEB_SOCKET_CLIENT_SENDER_LIST_TABLE = new Table<>();

    /**
     * 注册对应的操作器
     *
     * @param webSocketClientOperator WebSocketServer操作器
     */
    public void register(WebSocketClientOperator webSocketClientOperator) {
        Map<String, String> map = WebSocketUtil.parseWebSocketController(webSocketClientOperator);
        String loop = map.get("loop");
        String subject = map.get("subject");
        //两个值都不为空，则将其加入到对应的操作列中
        if (!StringUtils.isAnyEmpty(loop, subject)) {
            //添加到对应的发送器中
            if (webSocketClientOperator instanceof WebSocketClientSender) {
                //添加到对应的表格中去
                Set<WebSocketClientSender> webSocketClientSenderSet = WEB_SOCKET_CLIENT_SENDER_LIST_TABLE.get(loop, subject);
                if (null == webSocketClientSenderSet) {
                    webSocketClientSenderSet = new HashSet<>();
                    WEB_SOCKET_CLIENT_SENDER_LIST_TABLE.put(loop, subject, webSocketClientSenderSet);
                }
                webSocketClientSenderSet.add((WebSocketClientSender) webSocketClientOperator);
                //调用子项的回调方法，使得子对象可以获得客户端对象
                webSocketClientOperator.bind(this);
            }
            //添加对应的接收器中
            else if (webSocketClientOperator instanceof WebSocketClientReceiver) {
                //添加到对应的表格中去
                Set<WebSocketClientReceiver> webSocketClientReceiverSetSet = WEB_SOCKET_CLIENT_RECEIVER_LIST_TABLE.get(loop, subject);
                if (null == webSocketClientReceiverSetSet) {
                    webSocketClientReceiverSetSet = new HashSet<>();
                    WEB_SOCKET_CLIENT_RECEIVER_LIST_TABLE.put(loop, subject, webSocketClientReceiverSetSet);
                }
                webSocketClientReceiverSetSet.add((WebSocketClientReceiver) webSocketClientOperator);
                //调用子项的回调方法，使得子对象可以获得客户端对象
                webSocketClientOperator.bind(this);
            }
        }
    }

    private WebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Setter
    @Accessors(chain = true)
    public static class WebSocketClientBuilder {
        //目标的主机号
        private String host;
        //目标的端口号
        private String port;
        //头部参数
        @Setter(AccessLevel.NONE)
        private Map<String, String> openParamsMap = new HashMap<>();
        //链接打开时的监听器
        private Consumer<ServerHandshake> openListener;
        //收到信息时的监听器
        private Consumer<String> messageListener;
        //关闭时的监听器
        private Consumer<String> closeListener;
        //发生错误时的监听器
        private Consumer<Exception> errorListener;

        //添加头部参数
        public WebSocketClientBuilder addOpenParams(String key, String value) {
            openParamsMap.put(key, value);
            return this;
        }

        public WebSocketClient build() {
            if (!StringUtils.isAnyEmpty(host, port)) {
                WebSocketClient webSocketClient = new WebSocketClient(URI.create("ws://" + host + ":" + port)) {
                    @Override
                    public void onOpen(ServerHandshake handshakedata) {
                        logger.debug("打开与服务器连接");
                        //调用打开监听器
                        if (ObjectUtils.isNotEmpty(openListener)) {
                            openListener.accept(handshakedata);
                        }
                    }

                    @Override
                    public void onMessage(String message) {
                        logger.debug("接收与服务器信息");
                        //调用信息监听器
                        if (ObjectUtils.isNotEmpty(messageListener)) {
                            messageListener.accept(message);
                        }
                        //将JSON转换为WebSocketPackage
                        WebSocketPackage webSocketPackage = SerializeUtil.convertJsonToBeanByClass(message, WebSocketPackage.class);
                        // 获得所有的对应的接收器
                        Set<WebSocketClientReceiver> webSocketClientReceivers =
                                WEB_SOCKET_CLIENT_RECEIVER_LIST_TABLE.get(webSocketPackage.getLoop(), webSocketPackage.getSubject());
                        //遍历接收器
                        for (WebSocketClientReceiver webSocketClientReceiver : webSocketClientReceivers) {
                            //调用回调函数
                            webSocketClientReceiver.receive(webSocketPackage);
                        }
                    }

                    @Override
                    public void onClose(int code, String reason, boolean remote) {
                        logger.debug("关闭与服务器连接");
                        //调用关闭监听器
                        if (ObjectUtils.isNotEmpty(closeListener)) {
                            closeListener.accept(reason);
                        }
                    }

                    @Override
                    public void onError(Exception ex) {
                        logger.debug("与服务器连接发送错误");
                        //调用错误监听器
                        if (ObjectUtils.isNotEmpty(errorListener)) {
                            errorListener.accept(ex);
                        }
                    }

                    @Override
                    public void onWebsocketPing(WebSocket conn, Framedata f) {
                        logger.debug("WebSocket客户端发送Ping——" + conn.toString());
                        //发送心跳包
                        super.onWebsocketPing(conn, f);
                    }

                    @Override
                    public void onWebsocketPong(WebSocket conn, Framedata f) {
                        logger.debug("WebSocket客户端接收Pong——" + conn.toString());
                        try {
                            //线程睡眠15s
                            Thread.sleep(15 * 1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //发送心跳包
                        this.onWebsocketPing(conn, f);
                    }
                };
                //添加头部参数
                for (Map.Entry<String, String> entry : openParamsMap.entrySet()) {
                    webSocketClient.addHeader(entry.getKey(), entry.getValue());
                }
                //返回客户端对象
                return webSocketClient;
            } else {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.WebSocketClientParamErrorException);
            }
        }
    }
}
