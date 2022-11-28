package utils.websocket.rmi;

import utils.websocket.WebSocketPackage;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 方法调用注册表，通过WebSocketPackage确定对应的futureTask @Author Skye @Date 2022/11/28 19:22
 */
public class CallMethodRegistry {
    // 用于存储对应的FutureTask
    private static final Map<String, ModifiableFutureTask<Object>> MODIFIABLE_FUTURE_TASK =
            new HashMap<>();

    /**
     * 将对应的调用方法数据包与FutureTask任务绑定
     *
     * @param webSocketPackage 数据包
     * @param modifiableFutureTask FutureTask对象
     */
    public static void register(
            WebSocketPackage webSocketPackage, ModifiableFutureTask<Object> modifiableFutureTask) {
        MODIFIABLE_FUTURE_TASK.put(webSocketPackage.getId(), modifiableFutureTask);
    }

    /**
     * 通过对应的WebSocketPackage的id 来获得对应的future task
     *
     * @param webSocketPackageId 数据包的id
     * @return 对应的FutureTask
     */
    public static ModifiableFutureTask<Object> get(String webSocketPackageId) {
        return MODIFIABLE_FUTURE_TASK.get(webSocketPackageId);
    }
}
