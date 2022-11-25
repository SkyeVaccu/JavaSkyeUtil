package exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description 自定义的异常类
 * @Author Skye
 * @Date 2022/11/25 10:57
 */
@Setter
@Getter
@AllArgsConstructor
public class SkyeUtilsException extends RuntimeException {
    // 错误信息
    private String message;
    // 错误代码
    private int errorCode;
}
