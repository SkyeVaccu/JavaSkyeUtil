package utils.reflect;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/** @Description 远程方法请求封装 @Author Skye @Date 2022/11/28 15:27 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RemoteMethodRequestWrapper implements Serializable {
    // 方法定义参数
    private MethodDefinition methodDefinition;
    // 传入的参数
    private Object[] args;
    // 服务端调用对象的键，如果是静态方法，则这个值应当为null
    private String callTargetObjectKey;
    // 指定需要响应的服务端的key，这里将会锚定Server为当前所连接的服务器，此为一个关键字，无法使用,否则可能会出现无法预估的问题
    private String callTargetEndKey;
}
