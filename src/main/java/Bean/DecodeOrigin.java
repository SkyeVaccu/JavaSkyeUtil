package Bean;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
/**
 * 用于构造解码器的解码元
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecodeOrigin {
    private String algOperator;
    private Algorithm alg = Algorithm.HMAC256(algOperator);
    private String issuer;
    private String audience;

    /**
     * 判断解码元初始内容是否缺乏必须的内容
     * @return 返回是否不为空
     */
    public boolean isNotEmpty(){
        return !(null==algOperator||null==issuer||null==audience);
    }
}
