package utils.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Map;

/**
 * @Description 解码信息
 * @Author Skye
 * @Date 2022/11/25 10:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class DecodeInfo {
    // 对应的算法密钥
    private String algOperator;
    // 签发者
    private String issuer;
    // 主题
    private String subject;
    // 用户单位
    private String audience;
    // 默认的签发时间是当前
    private Date issueTime;
    // 默认的过期时间为1天
    private Date expireAtTime;
    // 解析出来的数据
    private Map<String, Object> datas;
}
