package utils.web_socket;

import utils.collection.MapUtil;

import java.util.Map;

/**
 * @Description WebSocket相关工具类 @Author Skye @Date 2022/11/26 16:58
 */
public class WebSocketUtil {
    /**
     * 解析操作器上的WebSocketController注释
     *
     * @param webSocketOperator 操作器对象
     * @return 对应的回路和主题
     */
    public static Map<String, String> parseWebSocketController(
            WebSocketOperator webSocketOperator) {
        WebSocketController[] webSocketControllers =
                webSocketOperator.getClass().getAnnotationsByType(WebSocketController.class);
        // 获得其对应的回路和主题
        String loop = null;
        String subject = null;
        for (WebSocketController webSocketController : webSocketControllers) {
            loop = webSocketController.loop();
            subject = webSocketController.subject();
        }
        return MapUtil.of(
                "loop", loop,
                "subject", subject);
    }
}
