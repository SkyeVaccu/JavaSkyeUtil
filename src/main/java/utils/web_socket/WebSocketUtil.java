package utils.web_socket;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description WebSocket相关工具类
 * @Author Skye
 * @Date 2022/11/26 16:58
 */
public class WebSocketUtil {
    /**
     * 解析操作器上的注释
     *
     * @param webSocketOperator 操作器对象
     * @return 对应的回路和主题
     */
    public static Map<String, String> parseWebSocketController(WebSocketOperator webSocketOperator) {
        WebSocketController[] webSocketControllers = webSocketOperator.getClass().getAnnotationsByType(WebSocketController.class);
        // 获得其对应的回路和主题
        String loop = null;
        String subject = null;
        for (WebSocketController webSocketController : webSocketControllers) {
            loop = webSocketController.loop();
            subject = webSocketController.subject();
        }
        Map<String, String> map = new HashMap<>(2);
        map.put("loop", loop);
        map.put("subject", subject);
        return map;
    }
}
