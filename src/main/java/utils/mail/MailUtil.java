package utils.mail;

import log.SkyeLogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.regex.Pattern;

/**
 * @Description 邮件对象 @Author Skye @Date 2022/11/25 11:11
 */
public class MailUtil {
    private static final Logger logger = SkyeLogger.getLogger();

    /**
     * 判断输入的邮件地址是否符合格式
     *
     * @param mail 邮件地址
     * @return 是否符合格式
     */
    public static boolean isMailLegal(String mail) {
        if (StringUtils.isEmpty(mail)) {
            logger.error("邮件地址不能为空");
            throw new NullPointerException();
        }
        String pattern = "^[a-zA-Z\\d\\u4e00-\\u9fa5_-]+@[a-zA-Z\\d_-]+(\\.[a-zA-Z\\d_-]+)+$";
        return Pattern.matches(pattern, mail);
    }
}
