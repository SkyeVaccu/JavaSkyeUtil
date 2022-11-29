package utils.sms;

import log.SkyeLogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.regex.Pattern;

/**
 * @Description 短信工具类 @Author Skye @Date 2022/11/25 11:17
 */
public class SmsUtil {
    private static final Logger logger = SkyeLogger.getLogger();

    /**
     * 判断输入的手机号是否符合格式
     *
     * @param phoneNumber 手机号
     * @return 是否符合格式
     */
    public static boolean isPhoneNumberLegal(String phoneNumber) {
        if (StringUtils.isEmpty(phoneNumber)) {
            logger.error("手机号码不能为空");
            throw new NullPointerException();
        }
        String pattern =
                "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";
        return Pattern.matches(pattern, phoneNumber);
    }
}
