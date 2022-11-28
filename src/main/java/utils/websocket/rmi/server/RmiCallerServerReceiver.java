package utils.websocket.rmi.server;

import com.fasterxml.jackson.core.type.TypeReference;
import utils.SerializeUtil;
import utils.reflect.RemoteMethodResponseWrapper;
import utils.websocket.WebSocketController;
import utils.websocket.WebSocketPackage;
import utils.websocket.rmi.CallMethodRegistry;
import utils.websocket.rmi.ModifiableFutureTask;
import utils.websocket.rmi.RmiCallerReceiver;
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
    private List<WebSocketConnection> webSocketConnectionList = new ArrayList<>();

    @Override
    public void bind(WebSocketConnection webSocketConnection) {
        this.webSocketConnectionList.add(webSocketConnection);
        webSocketConnection.bind(this);
    }

    @Override
    public void receive(
            WebSocketConnection webSocketConnection, WebSocketPackage webSocketPackage) {
        // 获得该结果响应的数据包
        String responseId = webSocketPackage.getResponseId();
        // 获得需要调用的FutureTask
        ModifiableFutureTask<Object> modifiableFutureTask = CallMethodRegistry.get(responseId);
        // 获得响应对象
        RemoteMethodResponseWrapper remoteMethodResponseWrapper =
                SerializeUtil.convertMapToBeanByClass(
                        (Map<String, Object>) webSocketPackage.getContent(),
                        new TypeReference<>() {});
        // TODO 需要对返回值进行反序列化
        Object returnValue = remoteMethodResponseWrapper.getReturnValue();
        // 设置对应的result
        modifiableFutureTask.setResult(returnValue);
        // TODO 将任务提交到线程池执行，使得可以返回值
        new Thread(modifiableFutureTask).start();
    }
}
