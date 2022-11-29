package utils.reflect;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 将方法对象抽象成便于传输的数据的形式 @Author Skye @Date 2022/11/28 10:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MethodDefinition implements Serializable {
    // 获得类名
    private String className;
    // 方法名,完整路径
    private String methodName;
    // 参数名及参数类型的全类名
    private List<String> parameterList;
    // 返回值类型的全类名
    private String returnClass;
}
