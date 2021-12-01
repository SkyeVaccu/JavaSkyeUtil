package SkyeUtilsException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 自定义的异常类
 */
@Setter
@Getter
@AllArgsConstructor
public class SkyeUtilsException extends RuntimeException {
    private String message;
    private int errorCode;
}
