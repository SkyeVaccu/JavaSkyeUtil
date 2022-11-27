package utils.websocket.server;

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
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.HandshakedataImpl1;
import org.slf4j.Logger;
import utils.AsyncUtil;
import utils.SerializeUtil;
import utils.collection.MapUtil;
import utils.collection.Table;
import utils.websocket.WebSocketPackage;
import utils.websocket.WebSocketUtil;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * @Description WebSocket服务器 @Author Skye @Date 2022/11/25 22:31
 */
public abstract class WebSocketServer extends org.java_websocket.server.WebSocketServer {
    private static final Logger logger = SkyeLogger.getLogger();

    // websocket列表，存储所有的webSocketConnection，其含有的操作器必定是绑定完成的
    protected final List<WebSocketConnection> WEB_SOCKET_CONNECTION_LIST = new ArrayList<>();
    // websocket 接收者table，通过loop,subject确定接收器，只有绑定过WebConnection才会被添加，即使这个连接已经中断
    // 因为一个操作器是单例的，其对应着多个连接，即便部分连接关闭，其也应当接收信息，当全部连接关闭时，其也不会再接收到信息被调用
    protected final Table<String, String, Set<WebSocketServerReceiver>>
            WEB_SOCKET_SERVER_RECEIVER_LIST_TABLE = new Table<>();
    // websocket 发送者table，通过loop,subject确定发送器，只有绑定过WebConnection才会被添加，即使这个连接已经中断
    protected final Table<String, String, Set<WebSocketServerSender>>
            WEB_SOCKET_SERVER_SENDER_LIST_TABLE = new Table<>();
    // 注册的所有的websocket Server operator，可能是绑定完成的，也可能是没有完成的
    protected final List<WebSocketServerOperator> WEB_SOCKET_SERVER_OPERATOR_LIST =
            new ArrayList<>();
    // 检查活动的延迟时间
    @Setter(AccessLevel.PRIVATE)
    private int checkDelayTime;
    // 检查活动的周期时间
    @Setter(AccessLevel.PRIVATE)
    private int checkPeriodTime;
    // 检查活动的非活跃时间
    @Setter(AccessLevel.PRIVATE)
    private int notActiveThreshold;

    public WebSocketServer(InetSocketAddress address) {
        super(address);
    }

    /**
     * 判断当前连接是否活跃
     *
     * @param webSocketConnection 连接对象
     * @return 是否活跃
     */
    public boolean webSocketConnectionActive(WebSocketConnection webSocketConnection) {
        return (!webSocketConnection.getWebSocket().isClosed())
                && (WEB_SOCKET_CONNECTION_LIST.contains(webSocketConnection));
    }

    /**
     * 注册对应的操作器
     *
     * @param webSocketServerOperator WebSocketServer操作器
     */
    public void register(WebSocketServerOperator webSocketServerOperator) {
        WEB_SOCKET_SERVER_OPERATOR_LIST.add(webSocketServerOperator);
    }

    /**
     * 获得封装后的活跃连接
     *
     * @param webSocket 连接对象
     * @return 活跃连接对象
     */
    protected WebSocketConnection getActiveWebSocketConnection(WebSocket webSocket) {
        for (WebSocketConnection webSocketConnection : WEB_SOCKET_CONNECTION_LIST) {
            if (webSocketConnection.getWebSocket() == webSocket) {
                return webSocketConnection;
            }
        }
        return null;
    }

    /** 启动Websocket服务器 */
    public void start() {
        super.start();
        logger.debug("启动WebSocket服务器");
        // 启动一个任务，每隔一段时间秒删除超过时间秒没响应的连接
        AsyncUtil.submitTaskPeriod(
                () ->
                        WEB_SOCKET_CONNECTION_LIST.removeIf(
                                webSocketConnection ->
                                        System.currentTimeMillis()
                                                        - webSocketConnection.getUpdateTime()
                                                > notActiveThreshold * 1000L),
                checkDelayTime,
                checkPeriodTime,
                TimeUnit.SECONDS,
                null);
    }

    @Accessors(chain = true)
    @Setter
    public static class WebSocketServerBuilder {
        // 服务器的主机号
        private String host;
        // 服务器的端口号
        private String port;
        // 检查活动的延迟时间
        private int checkDelayTime = 3;
        // 检查活动的周期时间
        private int checkPeriodTime = 15;
        // 检查活动的非活跃时间
        private int notActiveThreshold = 30;
        // 链接打开时的监听器
        private BiConsumer<WebSocket, ClientHandshake> openListener;
        // 收到信息时的监听器
        private BiConsumer<WebSocket, String> messageListener;
        // 关闭时的监听器
        private BiConsumer<WebSocket, String> closeListener;
        // 发生错误时的监听器
        private BiConsumer<WebSocket, Exception> errorListener;
        // 服务器启动时的监听器
        private Runnable startListener;

        /**
         * 创建对应的WebSocketConnection连接
         *
         * @param conn 连接对象
         * @param handshake 握手对象
         * @return 返回值
         */
        private WebSocketConnection createWebSocketConnection(
                WebSocket conn, ClientHandshake handshake) {
            try {
                // 将对应的变量修改为可访问
                Field field = HandshakedataImpl1.class.getDeclaredField("map");
                field.setAccessible(true);
                // 获得对应的数据
                TreeMap<String, String> map = (TreeMap<String, String>) field.get(handshake);
                // 生成对应的连接
                return new WebSocketConnection()
                        .setOpenTime(System.currentTimeMillis())
                        .setOpenParamMap(map)
                        .setWebSocket(conn);
            } catch (Exception e) {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.WebSocketServerOpenEventException);
            }
        }

        /**
         * 获得活跃连接上绑定的所有操作器
         *
         * @param webSocketConnection 连接对象
         * @return 绑定的接收器和发送器
         */
        private Map<String, List<? extends WebSocketServerOperator>> getAllWebSocketOperators(
                WebSocketConnection webSocketConnection) {
            List<WebSocketServerReceiver> webSocketServerReceivers = new ArrayList<>();
            List<WebSocketServerSender> webSocketServerSenders = new ArrayList<>();
            // 遍历所有的
            for (WebSocketServerOperator webSocketServerOperator :
                    webSocketConnection.getWebSocketServerOperatorList()) {
                // 如果是接收器
                if (webSocketServerOperator instanceof WebSocketServerReceiver) {
                    webSocketServerReceivers.add((WebSocketServerReceiver) webSocketServerOperator);
                }
                // 如果是发送器
                else if (webSocketServerOperator instanceof WebSocketServerSender) {
                    webSocketServerSenders.add((WebSocketServerSender) webSocketServerOperator);
                }
            }
            return MapUtil.of(
                    "webSocketReceivers", webSocketServerReceivers,
                    "webSocketSenders", webSocketServerSenders);
        }

        public WebSocketServer build() {
            if (StringUtils.isAnyEmpty(host, port)) {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.WebSocketServerParamErrorException);
            }
            WebSocketServer webSocketServer =
                    new WebSocketServer(new InetSocketAddress(host, Integer.parseInt(port))) {
                        @Override
                        public void onOpen(WebSocket conn, ClientHandshake handshake) {
                            logger.debug("客户端连接打开——" + conn.toString());
                            // 调用打开监听器
                            if (ObjectUtils.isNotEmpty(openListener)) {
                                openListener.accept(conn, handshake);
                            }
                            // 创建对应的连接对象
                            WebSocketConnection webSocketConnection =
                                    createWebSocketConnection(conn, handshake);
                            // 连接列表中增加该连接
                            WEB_SOCKET_CONNECTION_LIST.add(webSocketConnection);
                            // 调用所有的bind方法，将WebSocketOperator与WebSocketConnection进行绑定
                            // 由每个操作器，自己决定是否绑定，当完成绑定后，操作器会存储在WebSocketConnection连接中
                            for (WebSocketServerOperator webSocketServerOperator :
                                    WEB_SOCKET_SERVER_OPERATOR_LIST) {
                                webSocketServerOperator.bind(webSocketConnection);
                            }
                            // 遍历所有的连接，找到所有已绑定的WebSocketOperator
                            for (WebSocketConnection perWebSocketConnection :
                                    WEB_SOCKET_CONNECTION_LIST) {
                                // 获得活跃连接上绑定的所有操作器
                                Map<String, List<? extends WebSocketServerOperator>> map =
                                        getAllWebSocketOperators(perWebSocketConnection);
                                List<WebSocketServerReceiver> webSocketServerReceivers =
                                        (List<WebSocketServerReceiver>)
                                                map.get("webSocketReceivers");
                                List<WebSocketServerSender> webSocketServerSenders =
                                        (List<WebSocketServerSender>) map.get("webSocketSenders");
                                // 遍历所有接收器
                                for (WebSocketServerReceiver webSocketServerReceiver :
                                        webSocketServerReceivers) {
                                    // 解析注释
                                    Map<String, String> webSocketControllerMap =
                                            WebSocketUtil.parseWebSocketController(
                                                    webSocketServerReceiver);
                                    String loop = webSocketControllerMap.get("loop");
                                    String subject = webSocketControllerMap.get("subject");
                                    // 两个值都不为空，则将其加入到对应的操作列中
                                    if (!StringUtils.isAnyEmpty(loop, subject)) {
                                        // 添加到对应的表格中去
                                        Set<WebSocketServerReceiver> webSocketServerReceiverSet =
                                                WEB_SOCKET_SERVER_RECEIVER_LIST_TABLE.get(
                                                        loop, subject);
                                        if (null == webSocketServerReceiverSet) {
                                            webSocketServerReceiverSet = new HashSet<>();
                                            WEB_SOCKET_SERVER_RECEIVER_LIST_TABLE.put(
                                                    loop, subject, webSocketServerReceiverSet);
                                        }
                                        webSocketServerReceiverSet.add(webSocketServerReceiver);
                                    }
                                }
                                // 遍历所有发送器
                                for (WebSocketServerSender webSocketServerSender :
                                        webSocketServerSenders) {
                                    // 解析注释
                                    Map<String, String> webSocketControllerMap =
                                            WebSocketUtil.parseWebSocketController(
                                                    webSocketServerSender);
                                    String loop = webSocketControllerMap.get("loop");
                                    String subject = webSocketControllerMap.get("subject");
                                    // 两个值都不为空，则将其加入到对应的操作列中
                                    if (!StringUtils.isAnyEmpty(loop, subject)) {
                                        // 添加到对应的表格中去
                                        Set<WebSocketServerSender> webSocketServerSenderSet =
                                                WEB_SOCKET_SERVER_SENDER_LIST_TABLE.get(
                                                        loop, subject);
                                        if (null == webSocketServerSenderSet) {
                                            webSocketServerSenderSet = new HashSet<>();
                                            WEB_SOCKET_SERVER_SENDER_LIST_TABLE.put(
                                                    loop, subject, webSocketServerSenderSet);
                                        }
                                        webSocketServerSenderSet.add(webSocketServerSender);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onClose(
                                WebSocket conn, int code, String reason, boolean remote) {
                            try {
                                logger.debug("客户端连接关闭——" + conn.toString());
                                // 调用关闭监听器
                                if (ObjectUtils.isNotEmpty(closeListener)) {
                                    closeListener.accept(conn, reason);
                                }
                                // 找到对应的活跃连接
                                WebSocketConnection activeWebSocketConnection =
                                        getActiveWebSocketConnection(conn);
                                if (ObjectUtils.isNotEmpty(activeWebSocketConnection)) {
                                    // 设置关闭时间
                                    activeWebSocketConnection.setCloseTime(
                                            System.currentTimeMillis());
                                    // 调用关闭监听器
                                    if (ObjectUtils.isNotEmpty(
                                            activeWebSocketConnection.getCloseListener())) {
                                        activeWebSocketConnection.getCloseListener().accept(reason);
                                    }
                                    // 删除当前的连接
                                    WEB_SOCKET_CONNECTION_LIST.remove(activeWebSocketConnection);
                                }
                            } catch (Exception e) {
                                throw SkyeUtilsExceptionFactory.createException(
                                        SkyeUtilsExceptionType.WebSocketServerCloseEventException);
                            }
                        }

                        @Override
                        public void onMessage(WebSocket conn, String message) {
                            try {
                                logger.debug("客户端连接接收信息——" + conn.toString());
                                // 调用信息监听器
                                if (ObjectUtils.isNotEmpty(messageListener)) {
                                    messageListener.accept(conn, message);
                                }
                                // 找到对应的活跃连接
                                WebSocketConnection activeWebSocketConnection =
                                        getActiveWebSocketConnection(conn);
                                if (ObjectUtils.isNotEmpty(activeWebSocketConnection)) {
                                    // 设置更新时间
                                    activeWebSocketConnection.setUpdateTime(
                                            System.currentTimeMillis());
                                    // 调用接收信息监听器
                                    if (ObjectUtils.isNotEmpty(
                                            activeWebSocketConnection.getMessageListener())) {
                                        activeWebSocketConnection
                                                .getMessageListener()
                                                .accept(message);
                                    }
                                    // 将JSON转换为WebSocketPackage
                                    WebSocketPackage webSocketPackage =
                                            SerializeUtil.convertJsonToBeanByClass(
                                                    message, WebSocketPackage.class);
                                    // 获得所有的对应的接收器
                                    Set<WebSocketServerReceiver> webSocketServerReceivers =
                                            WEB_SOCKET_SERVER_RECEIVER_LIST_TABLE.get(
                                                    webSocketPackage.getLoop(),
                                                    webSocketPackage.getSubject());
                                    // 遍历接收器
                                    for (WebSocketServerReceiver webSocketServerReceiver :
                                            webSocketServerReceivers) {
                                        // 调用回调函数
                                        webSocketServerReceiver.receive(
                                                activeWebSocketConnection, webSocketPackage);
                                    }
                                }
                            } catch (Exception e) {
                                throw SkyeUtilsExceptionFactory.createException(
                                        SkyeUtilsExceptionType
                                                .WebSocketServerMessageEventException);
                            }
                        }

                        @Override
                        public void onError(WebSocket conn, Exception ex) {
                            try {
                                logger.debug("客户端连接发生错误——" + conn.toString());
                                // 调用错误监听器
                                if (ObjectUtils.isNotEmpty(errorListener)) {
                                    errorListener.accept(conn, ex);
                                }
                                // 找到对应的活跃连接
                                WebSocketConnection activeWebSocketConnection =
                                        getActiveWebSocketConnection(conn);
                                if (ObjectUtils.isNotEmpty(activeWebSocketConnection)) {
                                    // 设置关闭时间
                                    activeWebSocketConnection.setCloseTime(
                                            System.currentTimeMillis());
                                    // 调用错误监听器
                                    if (ObjectUtils.isNotEmpty(
                                            activeWebSocketConnection.getErrorListener())) {
                                        activeWebSocketConnection.getErrorListener().accept(ex);
                                    }
                                    // 删除当前的连接
                                    WEB_SOCKET_CONNECTION_LIST.remove(activeWebSocketConnection);
                                }
                            } catch (Exception e) {
                                throw SkyeUtilsExceptionFactory.createException(
                                        SkyeUtilsExceptionType.WebSocketServerErrorEventException);
                            }
                        }

                        @Override
                        public void onStart() {
                            try {
                                logger.debug("WebSocket服务器启动成功");
                                // 调用启动监听器
                                if (ObjectUtils.isNotEmpty(startListener)) {
                                    startListener.run();
                                }
                            } catch (Exception e) {
                                throw SkyeUtilsExceptionFactory.createException(
                                        SkyeUtilsExceptionType.WebSocketServerStartEventException);
                            }
                        }

                        @Override
                        public void onWebsocketPing(WebSocket conn, Framedata f) {
                            logger.debug("WebSocket服务器发送Ping——" + conn.toString());
                            // 发送心跳包
                            super.onWebsocketPing(conn, f);
                        }

                        @Override
                        public void onWebsocketPong(WebSocket conn, Framedata f) {
                            logger.debug("WebSocket服务器接收Pong——" + conn.toString());
                            // 接收到pong心跳包,继续发送ping命令
                            this.onWebsocketPing(conn, new PingFrame());
                            // 更新对应的连接的更新时间
                            getActiveWebSocketConnection(conn)
                                    .setUpdateTime(System.currentTimeMillis());
                        }
                    };
            // 分别设置各种时间
            webSocketServer.setCheckDelayTime(checkDelayTime);
            webSocketServer.setCheckPeriodTime(checkPeriodTime);
            webSocketServer.setNotActiveThreshold(notActiveThreshold);
            return webSocketServer;
        }
    }
}
