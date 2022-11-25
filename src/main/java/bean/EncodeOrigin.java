package bean;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Description 用于构造加密器的加密元
 * @Author Skye
 * @Date 2022/11/25 11:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EncodeOrigin {
    // 对应的算法密钥
    private String algOperator;
    // 指定对应的算法
    private Algorithm alg = Algorithm.HMAC256(algOperator);
    // 签发者
    private String issuer;
    // 主题
    private String subject;
    // 用户单位
    private String audience;
    // 默认的签发时间是当前
    private Date issueTime = new Date();
    // 默认的过期时间为1天
    private long expireTime = 24 * 3600 * 1000L;

    /**
     * 判断加密初始内容是否缺乏必须的内容
     *
     * @return 返回是否不为空
     */
    public boolean isNotEmpty() {
        return !(null == algOperator || null == issuer || null == subject || null == audience);
    }
}
