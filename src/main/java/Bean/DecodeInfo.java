package Bean;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Map;


/**
 * 解密之后出来的信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class DecodeInfo {
    private String algOperator;
    private String issuer;
    private String subject;
    private String audience;
    //默认的签发时间是当前
    private Date issueTime;
    //默认的过期时间为1天
    private Date expireAtTime;
    //解析出来的数据
    private Map<String,Object> datas;
}
