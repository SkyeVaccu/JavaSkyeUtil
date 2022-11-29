package utils.websocket;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description 用于标识操作器所在的回路和主题 @Author Skye @Date 2022/11/26 10:50
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WebSocketController {
    // 指定对应的回路
    String loop();

    // 指定对应的主题
    String subject();
}
