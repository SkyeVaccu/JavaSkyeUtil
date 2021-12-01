package Utils;

import Log.SkyeLogger;
import SkyeUtilsException.SkyeUtilsExceptionFacoty;
import SkyeUtilsException.SkyeUtilsExceptionType;
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

@Setter
@Accessors(chain = true)
public class MailUtil {

    //日志组件，用于打印对应的错误日志
    private final static Logger logger= SkyeLogger.getLogger();

    private Properties properties;
    private boolean startDebug=false;
    private Session session;
    private String username;
    private String password;

    /**
     * 判断输入的邮件地址是否符合格式
     * @param mail 邮件地址
     * @return 是否符合格式
     */
    public static boolean isMailLegal(String mail){
        if(StringUtils.isEmpty(mail)) {
            logger.error("邮件地址不能为空");
            throw new NullPointerException();
        }
        String pattern="^\\\\w+((-\\\\w+)|(\\\\.\\\\w+))*\\\\@[A-Za-z0-9]+((\\\\.|-)[A-Za-z0-9]+)*\\\\.[A-Za-z0-9]+$";
        return Pattern.matches(pattern, mail);
    }

    /**
     * 初始化邮件的配置
     * @param mailHost 用于发送的主机邮箱地址
     * @param protocol 发送邮件所使用的协议
     * @param port 发送所选择的端口
     * @param username 对应邮箱的用户名
     * @param password 对应邮箱的密码
     * @throws Exception
     */
    public void configProperties(String mailHost,String protocol,String port,String username,String password) throws Exception {
        properties = new Properties();
        if(StringUtils.isAnyEmpty(mailHost,protocol,port,username,password))
            throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.EmailPropertiesErrorException);
        properties.setProperty("mail.host",mailHost);
        properties.setProperty("mail.transport.protocol",protocol);
        if("smtp".equals(protocol)) {
            properties.setProperty("mail.smtp.auth", "true");
            properties.setProperty("mail.smtp.port", port);
        }
        this.username=username;
        this.password=password;
        session = Session.getInstance(properties);
    }

    /**
     * 发送文件
     * @param message 需要发送的邮件信息
     * @throws Exception
     */
    public void sendMail(Message message) throws Exception {
        if(null==message){
            logger.error("邮件对象不能为null");
            throw new NullPointerException();
        }
        //检查是否初始化
        if(null==session||null==properties||StringUtils.isAnyEmpty(username,password))
            throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.EmailNotInitException);
        //设置为Debug模式，从而能够查看邮件的发送状态
        session.setDebug(startDebug);
        //根据当前的环境获得一个运输对象
        Transport transport = session.getTransport();
        //连接到发送者的邮箱
        transport.connect(properties.getProperty("mail.host"),username,password);
        //发送对应的文件，发送给指定的用户
        transport.sendMessage(message,message.getAllRecipients());
        transport.close();
    }

    /**
     * 创建一个简单邮件
     * @param subject 邮件的主题
     * @param content 邮件的内容
     * @param mails 所有的收件人
     * @return 返回一个邮件对象
     * @throws Exception
     */
    public Message createSimpleMessage(String subject,String content,String... mails) throws Exception {
        if(StringUtils.isAnyEmpty(subject,content))
            throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.MessageSubjectAndCotentErrorException);
        //检查是否初始化
        if(null==session)
            throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.EmailNotInitException);
        MimeMessage message = new MimeMessage(session);
        //指定发送人
        message.setFrom(new InternetAddress(properties.getProperty("mail.host")));
        //讲所有的收件人地址进行转换
        List<Address> addressList = new LinkedList<>();
        for (String mail : mails) {
            //检查邮件地址
            if(!isMailLegal(mail))
                throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.EmailIsIllegalException);
            addressList.add(new InternetAddress(mail));
        }
        //指定收件人
        message.setRecipients(Message.RecipientType.TO, (Address[]) addressList.toArray());
        //指定主题
        message.setSubject(subject);
        //指定对应的内容
        message.setContent(content,"text/html;charset=UTF-8");
        return message;
    }
}
