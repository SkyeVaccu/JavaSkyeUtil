package Bean;

import Log.SkyeLogger;
import SkyeUtilsException.SkyeUtilsException;
import Utils.SeriliazeUtil;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于包装返回对象
 */
@Data
public class ResponseResult {

    //日志组件，用于打印对应的错误日志
    private final static Logger logger= SkyeLogger.getLogger();

    //当前返回结果的状态
    private int code;
    //对于当前结果的描述
    private String script;
    //JSON数据
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

    //设置一个使用异常的构造方法
    public ResponseResult(Throwable e){
        if(e instanceof SkyeUtilsException) {
            this.code=((SkyeUtilsException) e).getErrorCode();
            this.script = e.getMessage();
        }
        else {
            this.code=99999;
            this.script = "系统内部错误";
        }
    }

    //重写toString方法
    @Override
    public String toString() {
        try {
            Map<String, Object> data = new HashMap<>(){
                {put("code",code);put("script",script);}
            };
            if(null!=responseData)
                data.put("responseData",responseData);
            return SeriliazeUtil.convertObjectToJson(data);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("数据序列化错误");
            //如果序列化错误，直接犯错错误
            return "{\"code\":99999,\"script\":\"系统内部错误\"}";
        }
    }

}
