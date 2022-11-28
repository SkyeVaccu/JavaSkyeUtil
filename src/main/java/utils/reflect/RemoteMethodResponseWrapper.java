package utils.reflect;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 远程方法响应封装 @Author Skye @Date 2022/11/28 15:33
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RemoteMethodResponseWrapper {
    // 返回的值的类型必须继承了Serializable接口，否则无法进行序列化
    private Object returnValue;
}
