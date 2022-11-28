package utils.reflect;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * @Description @Author Skye @Date 2022/11/28 14:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallObjectMethod implements CallMethod {

    private Method method;
}
