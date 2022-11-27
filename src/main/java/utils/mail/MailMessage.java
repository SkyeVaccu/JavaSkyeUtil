package utils.mail;

import javax.mail.Message;

/**
 * @Description 邮件信息接口 @Author Skye @Date 2022/11/27 12:57
 */
public interface MailMessage {
    /**
     * 获得真实的邮件对象
     *
     * @return 邮件对象
     */
    Message getMessage();
}
