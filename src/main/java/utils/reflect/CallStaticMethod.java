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
public class CallStaticMethod implements CallMethod {

    // 目标方法对象
    private Method method;

    /**
     * 调用目标方法
     *
     * @param parameters 传入的参数
     */
    public Object call(Object... parameters) {
        Parameter[] methodParameters = method.getParameters();
        for (int i = 0; i < methodParameters.length; i++) {
            Parameter targetParameter = methodParameters[i];
            Object inputParameter = parameters[i];
            // 判断传入的参数是否与目标参数的类型适配
            if (!inputParameter.getClass().getName().equals(targetParameter.getName())) {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.ParameterClassNotCorrespondException);
            }
        }
        try {
            return method.invoke(null, parameters);
        } catch (Exception e) {
            e.printStackTrace();
            throw SkyeUtilsExceptionFactory.createException(
                    SkyeUtilsExceptionType.StaticMethodCallErrorException);
        }
    }
}
