package utils.reflect;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description 远程方法请求封装 @Author Skye @Date 2022/11/28 15:27
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RemoteMethodRequestWrapper implements Serializable {
    // 方法定义参数
    private MethodDefinition methodDefinition;
    // 传入的参数
    private Object[] args;
    // 服务端调用对象的键，如果是静态方法，则这个值应当为null
    private String callTargetKey;
}
