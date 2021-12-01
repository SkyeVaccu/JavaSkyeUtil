package Bean;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

/**
 * 用于构造加密器的加密元
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EncodeOrigin {
    private String algOperator;
    //指定对应的算法
    private Algorithm alg = Algorithm.HMAC256(algOperator);
    private String issuer;
    private String subject;
    private String audience;
    //默认的签发时间是当前
    private Date issueTime=new Date();
    //默认的过期时间为1天
    private long expireTime=24*3600*1000L;

    /**
     * 判断加密初始内容是否缺乏必须的内容
     * @return 返回是否不为空
     */
    public boolean isNotEmpty(){
        return !(null==algOperator||null==issuer||null==subject||null==audience);
    }
}
