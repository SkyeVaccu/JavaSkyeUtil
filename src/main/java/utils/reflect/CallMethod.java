package utils.reflect;

import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utils.SerializeUtil;

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
            args[i] = handleDataConflict(parameterType, args[i]);
        }
        return args;
    }

    /**
     * 处理方法参数的矛盾
     *
     * @param targetType 目标数据类型
     * @param originValue 传入的数据值
     * @return 转换后符合目标的数据
     */
    private Object handleDataConflict(Class<?> targetType, Object originValue) {
        // 如果是枚举类型，找到对应的值
        if (targetType.isEnum()) {
            // 遍历所有的枚举变量
            for (Object enumConstant : targetType.getEnumConstants()) {
                if (enumConstant.toString().equals(originValue.toString())) {
                    return enumConstant;
                }
            }
            throw SkyeUtilsExceptionFactory.createException(
                    SkyeUtilsExceptionType.CanNotFindEnumConstantException);
        }
        // 处理其他的数据类型
        else {
            switch (targetType.getSimpleName()) {
                case "int":
                    return Integer.valueOf(originValue.toString());
                case "float":
                    return Float.valueOf(originValue.toString());
                case "double":
                    return Double.valueOf(originValue.toString());
                case "String":
                    return originValue.toString();
                case "short":
                    return Short.valueOf(originValue.toString());
                case "byte":
                    return Byte.valueOf(originValue.toString());
                case "char":
                    return originValue;
                case "boolean":
                    return Boolean.valueOf(originValue.toString());
                default:
                    return SerializeUtil.convertJsonToBeanByClass(
                            SerializeUtil.convertObjectToJson(originValue), targetType);
            }
        }
    }
}
