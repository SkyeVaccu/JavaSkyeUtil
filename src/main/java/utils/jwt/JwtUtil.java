package utils.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import utils.SerializeUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @Description 可逆的加解密算法 考虑到可能在整个项目中存在使用多个加密方式的情况下 @Author Skye @Date 2022/11/25 11:09
 */
public class JwtUtil {

    /**
     * 加密算法，不推荐在加密中使用复杂的对象
     *
     * @param encodeOrigin 需要传入的加密元
     * @param params 需要加密的数据
     * @return 加密后的字符串
     */
    public static String encode(EncodeOrigin encodeOrigin, Map<String, Object> params) {
        if (null == encodeOrigin
                || (!encodeOrigin.isNotEmpty())
                || null == params
                || 0 == params.size()) {
            throw SkyeUtilsExceptionFactory.createException(
                    SkyeUtilsExceptionType.LackEncodeInfoException);
        }
        JWTCreator.Builder builder =
                JWT.create()
                        // 发行者
                        .withIssuer(encodeOrigin.getIssuer())
                        // 表明主题是token
                        .withSubject(encodeOrigin.getSubject())
                        // 用户单位
                        .withAudience(encodeOrigin.getAudience())
                        // 签发时间
                        .withIssuedAt(encodeOrigin.getIssueTime())
                        // 设置有效期，一天有效期
                        .withExpiresAt(
                                new Date(
                                        encodeOrigin.getIssueTime().getTime()
                                                + encodeOrigin.getExpireTime()));
        // 添加对应的自定义参数
        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            builder.withClaim(key, SerializeUtil.convertObjectToJson(value));
        }
        return builder.sign(encodeOrigin.getAlg());
    }

    /**
     * 解密算法
     *
     * @param encodeStr 需要解密的字符串
     * @param decodeOrigin 解密时的解密元
     * @param paramClasses 用户需要的数据
     * @return 包装后的DecodeInfo
     */
    public static DecodeInfo decode(
            String encodeStr, DecodeOrigin decodeOrigin, Map<String, Class<?>> paramClasses) {
        if ((!decodeOrigin.isNotEmpty()) || null == encodeStr || null == paramClasses) {
            throw SkyeUtilsExceptionFactory.createException(
                    SkyeUtilsExceptionType.LackDecodeInfoException);
        }
        // 根据指定算法，构建一个验证器
        JWTVerifier verifier =
                JWT.require(decodeOrigin.getAlg())
                        // 指定发行者
                        .withIssuer(decodeOrigin.getIssuer())
                        // 指定域名
                        .withAudience(decodeOrigin.getAudience())
                        .build();
        try {
            // 验证token是不是自己发送的
            verifier.verify(encodeStr);
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            throw SkyeUtilsExceptionFactory.createException(
                    SkyeUtilsExceptionType.DecodeStrException);
        }
        // 尝试解码
        Map<String, Object> decodeDataMap = new HashMap<>(paramClasses.size());
        // 进行解码
        DecodedJWT originStr = JWT.decode(encodeStr);
        // 添加元数据
        DecodeInfo decodeInfo =
                new DecodeInfo()
                        .setAlgOperator(originStr.getAlgorithm())
                        .setAudience(originStr.getAudience().get(0))
                        .setExpireAtTime(originStr.getExpiresAt())
                        .setIssuer(originStr.getIssuer())
                        .setIssueTime(originStr.getIssuedAt())
                        .setSubject(originStr.getSubject());
        // 遍历用户需要的信息，然后反序列化获得对应的值
        for (Entry<String, Class<?>> entry : paramClasses.entrySet()) {
            String key = entry.getKey();
            // 如果不存在对应的值，则抛出异常
            if (null == originStr.getClaim(key)) {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.NotExistDesignatedDataException);
            }
            Class<?> paramClass = entry.getValue();
            Object value =
                    SerializeUtil.convertJsonToBeanByClass(
                            originStr.getClaim(key).asString(), paramClass);
            decodeDataMap.put(key, value);
        }
        decodeInfo.setDatas(decodeDataMap);
        return decodeInfo;
    }
}
