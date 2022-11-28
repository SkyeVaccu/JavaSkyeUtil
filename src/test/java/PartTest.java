import org.junit.Test;
import utils.SerializeUtil;
import utils.reflect.MethodDefinition;
import utils.reflect.ReflectUtil;
import utils.string.StringUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** @Description @Author Skye @Date 2022/11/28 10:34 */
public class PartTest {

    @Test
    public void testReflectUtil() throws ClassNotFoundException, NoSuchMethodException {
        ArrayList<MethodDefinition> methodDefinitions = new ArrayList<>();
        for (Method method : StringUtil.class.getMethods()) {
            MethodDefinition methodDefinition = ReflectUtil.abstractMethod(method);
            methodDefinitions.add(methodDefinition);
            System.out.println(SerializeUtil.convertObjectToJson(methodDefinition));
        }
        for (MethodDefinition methodDefinition : methodDefinitions) {
            Class<?> aClass = Class.forName(methodDefinition.getClassName());
            List<Class<?>> parameterClassList =
                    methodDefinition.getParameterList().stream()
                            .map(ReflectUtil::getDataClassByName)
                            .collect(Collectors.toList());
            Method method =
                    aClass.getMethod(
                            methodDefinition.getMethodName(),
                            parameterClassList.toArray(Class[]::new));
            System.out.println(method);
        }
    }
}
