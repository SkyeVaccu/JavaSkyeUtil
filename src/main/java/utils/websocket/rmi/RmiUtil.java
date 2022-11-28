package utils.websocket.rmi;

import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import log.SkyeLogger;
import org.slf4j.Logger;
import utils.reflect.CallMethod;
import utils.reflect.MethodDefinition;
import utils.reflect.ReflectUtil;
import utils.reflect.RemoteMethodRequestWrapper;

/**
 * @Description Rmi 工具类，完成对于数据包的解包以及调用目标方法，获得返回方法等 @Author Skye @Date 2022/11/28 21:28
 */
public class RmiUtil {
    private static final Logger logger = SkyeLogger.getLogger();

    /**
     * 调用目标的方法
     *
     * @param remoteMethodRequestWrapper 远程方法请求包装
     * @return 返回值
     */
    public static Object callRemoteMethodRequestWrapper(
            RemoteMethodRequestWrapper remoteMethodRequestWrapper) {
        try {
            // 获得方法的定义，将方法定义还原为方法
            MethodDefinition methodDefinition = remoteMethodRequestWrapper.getMethodDefinition();
            CallMethod callMethod = ReflectUtil.specificMethod(methodDefinition);
            // 获得调用该方法的参数
            Object[] args = remoteMethodRequestWrapper.getArgs();
            // 获得其所在接口的Class
            Class<?> aClass = Class.forName(methodDefinition.getClassName());
            // 获得调用对象Key，从注册表中找到对应的obj
            Object callTarget =
                    CallTargetRegistry.find(remoteMethodRequestWrapper.getCallTargetKey(), aClass);
            logger.debug("执行远程调用----" + methodDefinition.getMethodName());
            // 调用目标的方法,获得返回值
            return callMethod.call(callTarget, args);
        } catch (Exception e) {
            e.printStackTrace();
            throw SkyeUtilsExceptionFactory.createException(
                    SkyeUtilsExceptionType.CanNotFindClassException);
        }
    }
}
