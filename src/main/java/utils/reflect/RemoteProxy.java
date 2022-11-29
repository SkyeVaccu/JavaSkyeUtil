package utils.reflect;

import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import log.SkyeLogger;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import utils.websocket.WebSocketPackage;
import utils.websocket.client.WebSocketClient;
import utils.websocket.rmi.CallMethodRegistry;
import utils.websocket.rmi.ModifiableFutureTask;
import utils.websocket.server.WebSocketConnection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @Description 远程代理，用于生成对于接口的代理，客户端通过调用该代理对象，能够将对于方法的调用信息打包进一个WebSocketPackage中去
 * 然后发送到服务端，由服务端选择调用的对象，然后将其从包中获得对应的数据，然后返回。 @Author Skye @Date 2022/11/28 15:02
 */
@Setter
@Accessors(chain = true)
public class RemoteProxy implements InvocationHandler {
    private static final Logger logger = SkyeLogger.getLogger();

    // websocketClient对象,说明当前是webSocket客户端
    private WebSocketClient webSocketClient;
    // websocketConnection对象，说明当前是webSocket服务器
    private WebSocketConnection webSocketConnection;
    // 服务端调用对象的键，如果是静态方法，则这个值应当为null
    private String callTargetObjectKey;
    // 指定需要响应的服务端的key，如果想要触发所有的服务终端，这个值应该为空或者null
    private String callTargetEndKey;

    /**
     * 将对应的数据包应当进行封装，然后通过WebSocket发送出去，然后阻塞等待数据返回，或者是通过回调的方式获得数据
     * 所有形参的数据类型，应当都实现了Serializable接口，否则会出现无法序列化的问题，同时不应当出现回调函数的形式去获得返回值， 当返回类型为void时，其将不会等待获取返回值
     * 避免在形参和返回值中使用List或者Map，由于存在泛型擦除的原因，当自定义类型被存储到使用泛型来约束数据类型的容器中时，
     * 会出现无法反序列化，仍保持List,Map的形态，此时如果想要正常使用，需要手动对数据进行反序列化
     *
     * @param proxy 代理对象
     * @param method 调用的目标方法
     * @param args 传入的参数
     * @return 服务端的返回值
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        try {
            // 对该方法进行抽象
            MethodDefinition methodDefinition = ReflectUtil.abstractMethod(method);
            // 生成对应的数据包
            WebSocketPackage webSocketPackage =
                    new WebSocketPackage()
                            // 设置该包的id
                            .setId(UUID.randomUUID().toString())
                            // 设置数据包的内容
                            .setContent(
                                    new RemoteMethodRequestWrapper(
                                            methodDefinition,
                                            args,
                                            callTargetObjectKey,
                                            callTargetEndKey))
                            // 设置发送时间
                            .setTime(System.currentTimeMillis())
                            // 设置回路为远程方法调用
                            .setLoop("RemoteCallMethod")
                            // 设置主题为远程方法调用
                            .setSubject("RemoteCallMethod")
                            // 设置数据包的类型为请求
                            .setWebSocketPackageType(WebSocketPackage.WebSocketPackageType.REQUEST);
            // 如果客户端平台不为空，则发送数据包
            if (ObjectUtils.isNotEmpty(webSocketClient)) {
                // 发送该数据包
                webSocketClient.send(webSocketPackage);
                logger.debug("调用远程方法---" + methodDefinition.getMethodName());
            }
            // 如果客户端平台为空，则检查连接对象是否为空，此时当前为服务器对象
            else if (ObjectUtils.isNotEmpty(webSocketConnection)) {
                webSocketConnection.send(webSocketPackage);
                logger.debug("调用远程方法---" + methodDefinition.getMethodName());
            }
            // 不存在可发送的平台，则抛出异常
            else {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.RemoteCallClientNotExistException);
            }
            // 如果返回值是void,则不再等待对应的返回值，直接返回null
            if (method.getReturnType().getName().equals(void.class.getName())) {
                logger.debug("远程方法无返回值，直接返回");
                return null;
            }
            // 如果是非void，则提交一个FutureTask，然后阻塞等待返回值
            else {
                // 生成对应的FutureTask
                ModifiableFutureTask<Object> modifiableFutureTask = new ModifiableFutureTask<>();
                // 将数据包与该FutureTask进行绑定
                CallMethodRegistry.register(webSocketPackage, modifiableFutureTask);
                // 获得远程方法调用返回的值，这个方法会发生阻塞
                Object result = modifiableFutureTask.get();
                logger.debug(
                        "远程方法返回结果---" + methodDefinition.getMethodName() + "---返回值---" + result);
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw SkyeUtilsExceptionFactory.createException(
                    SkyeUtilsExceptionType.RemoteMethodCallErrorException);
        }
    }

    /**
     * 创建对应的接口的代理对象
     *
     * @param interfaceClass 接口类对象
     * @param <T> 接口类
     * @return 代理对象T
     */
    public static <T> T createProxyInstance(Class<T> interfaceClass, RemoteProxy remoteProxy) {
        return (T)
                Proxy.newProxyInstance(
                        interfaceClass.getClassLoader(), new Class[] {interfaceClass}, remoteProxy);
    }
}
