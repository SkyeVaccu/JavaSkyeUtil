package utils.websocket.rmi;

import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import org.apache.commons.lang3.ObjectUtils;
import utils.collection.MapUtil;
import utils.reflect.RemoteMethodRequestWrapper;
import utils.websocket.WebSocketPackage;

import java.util.HashMap;
import java.util.Map;

/** @Description 方法调用注册表，通过WebSocketPackage确定对应的futureTask @Author Skye @Date 2022/11/28 19:22 */
public class CallMethodRegistry {
    // 用于存储对应的FutureTask
    private static final Map<String, Map<String, Object>> CALL_METHOD_MAP = new HashMap<>();
    // 用于导向对应的FutureTask
    private static final String MODIFIABLE_FUTURE_TASK_KEY = "modifiableFutureTask";
    // 用于导向对应的请求包
    private static final String REMOTE_METHOD_REQUEST_WRAPPER_KEY = "remoteMethodRequestWrapper";

    /**
     * 将对应的调用方法数据包与FutureTask任务绑定
     *
     * @param webSocketPackage 数据包
     * @param modifiableFutureTask FutureTask对象
     */
    public static void register(
            WebSocketPackage webSocketPackage, ModifiableFutureTask<Object> modifiableFutureTask) {
        CALL_METHOD_MAP.put(
                webSocketPackage.getId(),
                // 存储modifiableFutureTask以及请求包
                MapUtil.of(
                        MODIFIABLE_FUTURE_TASK_KEY,
                        modifiableFutureTask,
                        REMOTE_METHOD_REQUEST_WRAPPER_KEY,
                        webSocketPackage.getContent()));
    }

    /**
     * 通过对应的WebSocketPackage的id 来获得对应的future task
     *
     * @param webSocketPackageId 数据包的id
     * @return modifiableFutureTask
     */
    public static ModifiableFutureTask<Object> getModifiableFutureTask(String webSocketPackageId) {
        Map<String, Object> map = CALL_METHOD_MAP.get(webSocketPackageId);
        if (ObjectUtils.isNotEmpty(map)) {
            return (ModifiableFutureTask<Object>) map.get(MODIFIABLE_FUTURE_TASK_KEY);
        }
        throw SkyeUtilsExceptionFactory.createException(
                SkyeUtilsExceptionType.RMIFutureTaskNotExistException);
    }

    /**
     * 通过对应的WebSocketPackage的id 来获得对应的请求包
     *
     * @param webSocketPackageId 数据包的id
     * @return 请求包
     */
    public static RemoteMethodRequestWrapper getRemoteMethodRequestWrapper(
            String webSocketPackageId) {
        Map<String, Object> map = CALL_METHOD_MAP.get(webSocketPackageId);
        if (ObjectUtils.isNotEmpty(map)) {
            return (RemoteMethodRequestWrapper) map.get(REMOTE_METHOD_REQUEST_WRAPPER_KEY);
        }
        throw SkyeUtilsExceptionFactory.createException(
                SkyeUtilsExceptionType.RMIRequestWrapperNotExistException);
    }

    /**
     * 移除掉已经响应的方法
     *
     * @param webSocketPackageId 已经响应的调用方法id
     */
    public static void remove(String webSocketPackageId) {
        CALL_METHOD_MAP.remove(webSocketPackageId);
    }
}
