package Utils;

import SkyeUtilsException.SkyeUtilsExceptionType;
import SkyeUtilsException.SkyeUtilsExceptionFacoty;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * 单向的加解密算法
 */
public class MD5Util {

    /**
     * 传入字符串进行加密
     * @param str 字符串
     * @return 加密后的字符串
     */
    public static String getMD5String(String str) throws Exception {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.MD5EncodeException);
        }
    }
}
