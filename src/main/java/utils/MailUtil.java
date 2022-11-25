package utils;

import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import log.SkyeLogger;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @Description 邮件对象
 * @Author Skye
 * @Date 2022/11/25 11:11
 */
@Setter
@Accessors(chain = true)
public class MailUtil {

    private static final Logger logger = SkyeLogger.getLogger();

    // 对应的STMP协议
    private static final String STMP_PROTOCOL = "stmp";

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
        String pattern = "^\\\\w+((-\\\\w+)|(\\\\.\\\\w+))*\\\\@[A-Za-z\\d]+((\\\\.|-)[A-Za-z\\d]+)*\\\\.[A-Za-z\\d]+$";
        return Pattern.matches(pattern, mail);
    }


    @Accessors(chain = true)
    @Setter
    public static class MailSessionBuilder{
        //发送者的邮箱
        private String mailHost;
        //指导的协议
        private String protocol;
        //对应的端口
        private String port;
        //发送者邮箱的用户名
        private String username;
        //发送者邮箱的密码
        private String password;
        //是否开启debug
        private boolean startDebug;

        public MailSession build(){
            Properties properties = new Properties();
            if (StringUtils.isAnyEmpty(mailHost, protocol, port, username, password)) {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.EmailPropertiesErrorException);
            }
            properties.setProperty("mail.host", mailHost);
            properties.setProperty("mail.transport.protocol", protocol);
            //如果是STMP协议
            if (STMP_PROTOCOL.equals(protocol)) {
                properties.setProperty("mail.smtp.auth", "true");
                properties.setProperty("mail.smtp.port", port);
            }
            //设置用户名密码
            properties.setProperty("username",username);
            properties.setProperty("password",password);
            //创建会话对象
            Session session=Session.getInstance(properties);
            // 设置为Debug模式，从而能够查看邮件的发送状态
            session.setDebug(startDebug);
            return new MailSession(session,properties);
        }
    }

    @Accessors(chain = true)
    @Setter
    public static class SimpleMailMessageBuilder{
        // 邮件的主题
        private String subject;
        // 邮件的内容
        private String content;
        //会话对象
        private MailSession mailSession;
        // 收件人
        private String[] mails;
        //邮件对象
        @Setter(AccessLevel.NONE)
        @Getter
        private Message message;

        public Message build() throws Exception{
            //检查主体和内容
            if (StringUtils.isAnyEmpty(subject, content)) {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.MessageSubjectAndCotentErrorException);
            }
            // 检查是否初始化
            if (null == mailSession) {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.EmailNotInitException);
            }
            message = new MimeMessage(mailSession.getSession());
            // 指定发送人
            message.setFrom(new InternetAddress(mailSession.getProperties().getProperty("mail.host")));
            // 讲所有的收件人地址进行转换
            List<Address> addressList = new LinkedList<>();
            for (String mail : mails) {
                // 检查邮件地址
                if (!isMailLegal(mail)) {
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
            return message;
        }
    }

    @AllArgsConstructor
    @Getter
    public static class MailSession{
        //会话对象
        private Session session;
        //配置对象
        private Properties properties;

        /**
         * 发送邮件
         *
         * @param message 需要发送的邮件信息
         * @throws Exception 邮件发送异常
         */
        public void sendMail(Message message) throws Exception {
            if (null == message) {
                logger.error("邮件对象不能为null");
                throw new NullPointerException();
            }
            // 检查是否初始化
            if (null == session || null == properties || StringUtils.isAnyEmpty(properties.getProperty("username"), properties.getProperty("password"))) {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.EmailNotInitException);
            }
            // 根据当前的环境获得一个运输对象
            Transport transport = session.getTransport();
            // 连接到发送者的邮箱
            transport.connect(properties.getProperty("mail.host"), properties.getProperty("username"), properties.getProperty("password"));
            // 发送对应的文件，发送给指定的用户
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        }
    }
}
