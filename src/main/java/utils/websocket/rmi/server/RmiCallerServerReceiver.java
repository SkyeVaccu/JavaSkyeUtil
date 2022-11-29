package utils.websocket.rmi.server;

import com.fasterxml.jackson.core.type.TypeReference;
import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import utils.AsyncUtil;
import utils.SerializeUtil;
import utils.reflect.ReflectUtil;
import utils.reflect.RemoteMethodResponseWrapper;
import utils.websocket.WebSocketController;
import utils.websocket.WebSocketPackage;
import utils.websocket.rmi.ModifiableFutureTask;
import utils.websocket.rmi.RmiCallerReceiver;
import utils.websocket.rmi.client.CallMethodRegistry;
import utils.websocket.server.WebSocketConnection;
import utils.websocket.server.WebSocketServerReceiver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description 服务端方法调用者，当当前服务端作为调用者时，其会接收到调用方法的返回值 @Author Skye @Date 2022/11/28 16:04
 */
@WebSocketController(loop = "RemoteCallMethod", subject = "RemoteCallMethod")
public class RmiCallerServerReceiver extends WebSocketServerReceiver implements RmiCallerReceiver {
    // 用于存储webSocketConnection的列表
    private final List<WebSocketConnection> webSocketConnectionList = new ArrayList<>();

    @Override
    public void bind(WebSocketConnection webSocketConnection) {
        // 将所有的连接都作为待调用对象，进行绑定
        this.webSocketConnectionList.add(webSocketConnection);
        webSocketConnection.bind(this);
    }

    @Override
    public void receive(
            WebSocketConnection webSocketConnection, WebSocketPackage webSocketPackage) {
        // 只会对响应包进行处理
        if (webSocketPackage
                .getWebSocketPackageType()
                .equals(WebSocketPackage.WebSocketPackageType.RESPONSE)) {
            // 获得该结果响应的数据包
            String responseId = webSocketPackage.getResponseId();
            // 获得需要调用的FutureTask
            ModifiableFutureTask<Object> modifiableFutureTask =
                    CallMethodRegistry.getModifiableFutureTask(responseId);
            // 获得响应对象
            RemoteMethodResponseWrapper remoteMethodResponseWrapper =
                    SerializeUtil.convertMapToBeanByClass(
                            (Map<String, Object>) webSocketPackage.getContent(),
                            new TypeReference<>() {});
            // 获得对应的返回值
            Object returnValue = remoteMethodResponseWrapper.getReturnValue();
            // 获得返回值的类型名
            String returnClassName =
                    CallMethodRegistry.getRemoteMethodRequestWrapper(responseId)
                            .getMethodDefinition()
                            .getReturnClass();
            try {
                // 解决数据转换矛盾
                Object conversionReturnValue =
                        ReflectUtil.handleDataConflict(returnClassName, returnValue);
                // 设置对应的result
                modifiableFutureTask.setResult(conversionReturnValue);
                // 将任务提交给线程池
                AsyncUtil.submitTask(modifiableFutureTask);
            } catch (Exception e) {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.CanNotFindClassException);
            }
        }
    }
}
