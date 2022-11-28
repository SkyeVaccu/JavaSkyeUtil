package utils.reflect;

import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/** @Description 包装目标方法 @Author Skye @Date 2022/11/28 14:22 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallMethod {

    // 目标方法对象
    private Method method;

    /**
     * 调用目标方法
     *
     * @param object 调用该方法的对象，如果是静态方法，传入值应当为null
     * @param args 传入的参数
     */
    public Object call(Object object, Object[] args) {
        try {
            // 处理对应的参数
            Object[] handledArgs = ReflectUtil.handleMethodParameter(method, args);
            // 调用目标方法
            return method.invoke(object, handledArgs);
        } catch (Exception e) {
            e.printStackTrace();
            throw SkyeUtilsExceptionFactory.createException(
                    SkyeUtilsExceptionType.RemoteMethodCallErrorException);
        }
    }
}
