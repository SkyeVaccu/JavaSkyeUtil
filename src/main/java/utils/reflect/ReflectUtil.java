package utils.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/** @Description 反射工具类 @Author Skye @Date 2022/11/27 17:32 */
public class ReflectUtil {

    /**
     * 将方法抽象成，简单用于传输的对象
     *
     * @param method 方法对象
     * @return 方法定义
     */
    public static MethodDefinition abstractMethod(Method method) {
        String className = method.getDeclaringClass().getName();
        // 获得方法名
        String methodClassName = method.getName();
        // 获得对应的参数值
        List<String> list = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            list.add(parameter.getType().getName());
        }
        // 获得返回值类型
        String returnClassName = method.getReturnType().getName();
        return new MethodDefinition(className, methodClassName, list, returnClassName);
    }
}
