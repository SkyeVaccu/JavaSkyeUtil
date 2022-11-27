package utils.websocket.client;

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
import org.java_websocket.framing.PingFrame;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import utils.AsyncUtil;
import utils.SerializeUtil;
import utils.collection.Table;
import utils.websocket.WebSocketPackage;
import utils.websocket.WebSocketUtil;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @Description WebSocket客户端 @Author Skye @Date 2022/11/26 15:50
 */
public abstract class WebSocketClient extends org.java_websocket.client.WebSocketClient {
    private static final Logger logger = SkyeLogger.getLogger();

    // websocket 接收者table，通过loop,subject确定接收器
    protected final Table<String, String, Set<WebSocketClientReceiver>>
            WEB_SOCKET_CLIENT_RECEIVER_LIST_TABLE = new Table<>();
    // websocket 发送者table，通过loop,subject确定发送器
    protected final Table<String, String, Set<WebSocketClientSender>>
            WEB_SOCKET_CLIENT_SENDER_LIST_TABLE = new Table<>();
    // 未响应的ping命令次数
    public int nonResponseCount = 0;
    // 心跳周期时间
    @Setter(AccessLevel.PRIVATE)
    private int heartbeatPeriod;
    // 检查活动的延迟时间
    @Setter(AccessLevel.PRIVATE)
    private int checkDelayTime;
    // 检查活动的周期时间
    @Setter(AccessLevel.PRIVATE)
    private int checkPeriodTime;
    // 检查活动的非活跃时间
    @Setter(AccessLevel.PRIVATE)
    private int notResponseThreshold;

    /**
     * 注册对应的操作器
     *
     * @param webSocketClientOperator WebSocketServer操作器
     */
    public void register(WebSocketClientOperator webSocketClientOperator) {
        Map<String, String> map = WebSocketUtil.parseWebSocketController(webSocketClientOperator);
        String loop = map.get("loop");
        String subject = map.get("subject");
        // 两个值都不为空，则将其加入到对应的操作列中
        if (!StringUtils.isAnyEmpty(loop, subject)) {
            // 添加到对应的发送器中
            if (webSocketClientOperator instanceof WebSocketClientSender) {
                // 添加到对应的表格中去
                Set<WebSocketClientSender> webSocketClientSenderSet =
                        WEB_SOCKET_CLIENT_SENDER_LIST_TABLE.get(loop, subject);
                if (null == webSocketClientSenderSet) {
                    webSocketClientSenderSet = new HashSet<>();
                    WEB_SOCKET_CLIENT_SENDER_LIST_TABLE.put(
                            loop, subject, webSocketClientSenderSet);
                }
                webSocketClientSenderSet.add((WebSocketClientSender) webSocketClientOperator);
                // 调用子项的回调方法，使得子对象可以获得客户端对象
                webSocketClientOperator.bind(this);
            }
            // 添加对应的接收器中
            else if (webSocketClientOperator instanceof WebSocketClientReceiver) {
                // 添加到对应的表格中去
                Set<WebSocketClientReceiver> webSocketClientReceiverSetSet =
                        WEB_SOCKET_CLIENT_RECEIVER_LIST_TABLE.get(loop, subject);
                if (null == webSocketClientReceiverSetSet) {
                    webSocketClientReceiverSetSet = new HashSet<>();
                    WEB_SOCKET_CLIENT_RECEIVER_LIST_TABLE.put(
                            loop, subject, webSocketClientReceiverSetSet);
                }
                webSocketClientReceiverSetSet.add(
                        (WebSocketClientReceiver) webSocketClientOperator);
                // 调用子项的回调方法，使得子对象可以获得客户端对象
                webSocketClientOperator.bind(this);
            }
        }
    }

    /**
     * 将对应的消息包转换为json然后发送
     *
     * @param webSocketPackage 对应的消息包
     */
    public void send(WebSocketPackage webSocketPackage) {
        this.send(SerializeUtil.convertObjectToJson(webSocketPackage));
    }

    /** 连接WebSocket服务器 */
    @Override
    public void connect() {
        super.connect();
        logger.debug("连接WebSocket服务器");
        // 每隔一段时间检查一下，如果服务器超过阈值没有返回响应的Pong命令，则进行重连
        AsyncUtil.submitTaskPeriod(
                () -> {
                    if (nonResponseCount > notResponseThreshold) {
                        logger.error("尝试重新连接WebSocket服务器");
                        this.connect();
                    }
                },
                checkDelayTime,
                checkPeriodTime,
                TimeUnit.SECONDS,
                null);
    }

    private WebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Setter
    @Accessors(chain = true)
    public static class WebSocketClientBuilder {
        // 目标的主机号
        private String host;
        // 目标的端口号
        private String port;
        // 心跳周期为15s
        private int hearBeatPeriod = 15;
        // 检查活动的延迟时间
        private int checkDelayTime = 3;
        // 检查活动的周期时间
        private int checkPeriodTime = 15;
        // 检查活动的非活跃时间
        private int notResponseThreshold = 3;
        // 头部参数
        @Setter(AccessLevel.NONE)
        private Map<String, String> openParamsMap = new HashMap<>();
        // 链接打开时的监听器
        private Consumer<ServerHandshake> openListener;
        // 收到信息时的监听器
        private Consumer<String> messageListener;
        // 关闭时的监听器
        private Consumer<String> closeListener;
        // 发生错误时的监听器
        private Consumer<Exception> errorListener;

        // 添加头部参数
        public WebSocketClientBuilder addOpenParams(String key, String value) {
            openParamsMap.put(key, value);
            return this;
        }

        public WebSocketClient build() {
            if (!StringUtils.isAnyEmpty(host, port)) {
                WebSocketClient webSocketClient =
                        new WebSocketClient(URI.create("ws://" + host + ":" + port)) {
                            @Override
                            public void onOpen(ServerHandshake handshakedata) {
                                logger.debug("打开与服务器连接");
                                // 调用打开监听器
                                if (ObjectUtils.isNotEmpty(openListener)) {
                                    openListener.accept(handshakedata);
                                }
                            }

                            @Override
                            public void onMessage(String message) {
                                logger.debug("接收与服务器信息");
                                // 调用信息监听器
                                if (ObjectUtils.isNotEmpty(messageListener)) {
                                    messageListener.accept(message);
                                }
                                // 将JSON转换为WebSocketPackage
                                WebSocketPackage webSocketPackage =
                                        SerializeUtil.convertJsonToBeanByClass(
                                                message, WebSocketPackage.class);
                                // 获得所有的对应的接收器
                                Set<WebSocketClientReceiver> webSocketClientReceivers =
                                        WEB_SOCKET_CLIENT_RECEIVER_LIST_TABLE.get(
                                                webSocketPackage.getLoop(),
                                                webSocketPackage.getSubject());
                                // 遍历接收器
                                for (WebSocketClientReceiver webSocketClientReceiver :
                                        webSocketClientReceivers) {
                                    // 调用回调函数
                                    webSocketClientReceiver.receive(webSocketPackage);
                                }
                            }

                            @Override
                            public void onClose(int code, String reason, boolean remote) {
                                logger.debug("关闭与服务器连接");
                                // 调用关闭监听器
                                if (ObjectUtils.isNotEmpty(closeListener)) {
                                    closeListener.accept(reason);
                                }
                            }

                            @Override
                            public void onError(Exception ex) {
                                logger.debug("与服务器连接发送错误");
                                // 调用错误监听器
                                if (ObjectUtils.isNotEmpty(errorListener)) {
                                    errorListener.accept(ex);
                                }
                            }

                            @Override
                            public void onWebsocketPing(WebSocket conn, Framedata f) {
                                logger.debug("WebSocket客户端发送Ping——" + conn.toString());
                                // 发送心跳包
                                super.onWebsocketPing(conn, f);
                                // 未响应数量+1
                                nonResponseCount++;
                            }

                            @Override
                            public void onWebsocketPong(WebSocket conn, Framedata f) {
                                logger.debug("WebSocket客户端接收Pong——" + conn.toString());
                                AsyncUtil.submitTaskDelayed(
                                        () -> {
                                            // 发送心跳包
                                            this.onWebsocketPing(conn, new PingFrame());
                                        },
                                        hearBeatPeriod,
                                        TimeUnit.SECONDS);
                                // 未响应数量-1
                                nonResponseCount--;
                            }
                        };
                // 添加头部参数
                for (Map.Entry<String, String> entry : openParamsMap.entrySet()) {
                    webSocketClient.addHeader(entry.getKey(), entry.getValue());
                }
                // 设置相关的时间参数
                webSocketClient.setHeartbeatPeriod(hearBeatPeriod);
                webSocketClient.setCheckDelayTime(checkDelayTime);
                webSocketClient.setCheckPeriodTime(checkPeriodTime);
                webSocketClient.setNotResponseThreshold(notResponseThreshold);
                // 返回客户端对象
                return webSocketClient;
            } else {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.WebSocketClientParamErrorException);
            }
        }
    }
}
