package utils.websocket.rmi.client;

import com.fasterxml.jackson.core.type.TypeReference;
import utils.SerializeUtil;
import utils.reflect.RemoteMethodResponseWrapper;
import utils.websocket.WebSocketController;
import utils.websocket.WebSocketPackage;
import utils.websocket.client.WebSocketClient;
import utils.websocket.client.WebSocketClientReceiver;
import utils.websocket.rmi.CallMethodRegistry;
import utils.websocket.rmi.ModifiableFutureTask;
import utils.websocket.rmi.RmiCallerReceiver;

import java.util.Map;

/**
 * @Description 客户端方法调用者，当当前客户端作为调用者时，其会接收到调用方法的返回值 @Author Skye @Date 2022/11/28 16:05
 */
@WebSocketController(loop = "RemoteCallMethod", subject = "RemoteCallMethod")
public class RmiCallerClientReceiver extends WebSocketClientReceiver implements RmiCallerReceiver {

    // WebSocketClient对象
    private WebSocketClient webSocketClient;

    @Override
    public void bind(WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    @Override
    public void receive(WebSocketPackage webSocketPackage) {
        // 获得该结果响应的数据包
        String responseId = webSocketPackage.getResponseId();
        // 获得需要调用的FutureTask
        ModifiableFutureTask<Object> modifiableFutureTask = CallMethodRegistry.get(responseId);
        // 获得响应对象
        RemoteMethodResponseWrapper remoteMethodResponseWrapper =
                SerializeUtil.convertMapToBeanByClass(
                        (Map<String, Object>) webSocketPackage.getContent(),
                        new TypeReference<>() {});
        // 设置对应的result
        modifiableFutureTask.setResult(remoteMethodResponseWrapper.getReturnValue());
        // TODO 将任务提交到线程池执行，使得可以返回值
        new Thread(modifiableFutureTask).start();
    }
}
