package utils.reflect;

import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @Description 包装目标方法 @Author Skye @Date 2022/11/28 14:22
 */
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
            Object[] handledArgs = handleMethodParameter(method, args);
            // 调用目标方法
            return method.invoke(object, handledArgs);
        } catch (Exception e) {
            e.printStackTrace();
            throw SkyeUtilsExceptionFactory.createException(
                    SkyeUtilsExceptionType.RemoteMethodCallErrorException);
        }
    }

    /**
     * 处理对应的参数，根据目标方法，对传入的参数进行处理，args已经是经过序列化和反序列化的，其可能存在反序列化错误的情况，尤其是当出现枚举对象等
     * 其会反序列化成String类型，对于这种情况，应当进行处理
     *
     * @param method 目标方法
     * @param args 调用的参数
     * @return 经过处理的参数
     */
    private Object[] handleMethodParameter(Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Class<?> parameterType = parameters[i].getType();
            // 处理参数序列化和反序列化的矛盾
            args[i] = ReflectUtil.handleDataConflict(parameterType, args[i]);
        }
        return args;
    }
}
