package bean;

import exception.SkyeUtilsException;
import log.SkyeLogger;
import lombok.Data;
import org.slf4j.Logger;
import utils.MapUtil;
import utils.SerializeUtil;

import java.util.Map;

/**
 * @Description 用于包装返回对象
 * @Author Skye
 * @Date 2022/11/25 10:56
 */
@Data
public class ResponseResult {

    private static final Logger logger = SkyeLogger.getLogger();

    // 当前返回结果的状态
    private int code;
    // 对于当前结果的描述
    private String script;
    // JSON数据
    private Object responseData;

    public ResponseResult(int code, String script) {
        this.code = code;
        this.script = script;
    }

    public ResponseResult(int code, String script, Object responseData) {
        this.code = code;
        this.script = script;
        this.responseData = responseData;
    }

    /**
     * 设置一个使用异常的构造方法
     */
    public ResponseResult(Throwable e) {
        if (e instanceof SkyeUtilsException) {
            this.code = ((SkyeUtilsException) e).getErrorCode();
            this.script = e.getMessage();
        } else {
            this.code = 99999;
            this.script = "系统内部错误";
        }
    }

    /**
     * 重写toString方法
     */
    @Override
    public String toString() {
        try {
            Map<Object, Object> data = MapUtil.of("code", code, "script", script);
            if (null != responseData) {
                data.put("responseData", responseData);
            }
            return SerializeUtil.convertObjectToJson(data);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("数据序列化错误");
            // 如果序列化错误，直接犯错错误
            return "{\"code\":99999,\"script\":\"系统内部错误\"}";
        }
    }
}
