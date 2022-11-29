package utils.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 用于构造解码器的解码元 @Author Skye @Date 2022/11/25 10:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecodeOrigin {
    // 对应的算法密钥
    private String algOperator;
    // 算法对象
    private Algorithm alg;
    // 主题
    private String issuer;
    // 用户单位
    private String audience;
    // default decode origin
    public static DecodeOrigin defaultDecodeOrigin;

    /**
     * 判断解码元初始内容是否缺乏必须的内容
     *
     * @return 返回是否不为空
     */
    public boolean isNotEmpty() {
        return !(null == algOperator || null == issuer || null == audience);
    }

    /**
     * get the default DecodeOrigin
     *
     * @return 默认的解密元
     */
    public static DecodeOrigin getDefaultEncodeOrigin() {
        // if the instance is null , we will create an object
        if (defaultDecodeOrigin == null) {
            defaultDecodeOrigin =
                    new DecodeOrigin("Skye", Algorithm.HMAC256("Skye"), "Skye", "vaccum.ltd");
        }
        return defaultDecodeOrigin;
    }
}
