package utils.mail;

import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.LinkedList;
import java.util.List;

/**
 * @Description 简单邮件信息 @Author Skye @Date 2022/11/25 20:18
 */
@AllArgsConstructor
public class SimpleMailMessage implements MailMessage {
    // 邮件对象
    private Message message;

    @Override
    public Message getMessage() {
        return message;
    }

    @Accessors(chain = true)
    @Setter
    public static class SimpleMailMessageBuilder {
        // 邮件的主题
        private String subject;
        // 邮件的内容
        private String content;
        // 会话对象
        private MailSession mailSession;
        // 收件人
        private String[] mails;

        public SimpleMailMessage build() throws Exception {
            // 检查主体和内容
            if (StringUtils.isAnyEmpty(subject, content)) {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.MessageSubjectAndCotentErrorException);
            }
            // 检查是否初始化
            if (null == mailSession) {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.EmailNotInitException);
            }
            Message message = new MimeMessage(mailSession.getSession());
            // 指定发送人
            message.setFrom(
                    new InternetAddress(mailSession.getProperties().getProperty("username")));
            // 讲所有的收件人地址进行转换
            List<Address> addressList = new LinkedList<>();
            for (String mail : mails) {
                // 检查邮件地址
                if (!MailUtil.isMailLegal(mail)) {
                    throw SkyeUtilsExceptionFactory.createException(
                            SkyeUtilsExceptionType.EmailIsIllegalException);
                }
                addressList.add(new InternetAddress(mail));
            }
            // 指定收件人
            message.setRecipients(Message.RecipientType.TO, addressList.toArray(new Address[0]));
            // 指定主题
            message.setSubject(subject);
            // 指定对应的内容
            message.setContent(content, "text/html;charset=UTF-8");
            return new SimpleMailMessage(message);
        }
    }
}
