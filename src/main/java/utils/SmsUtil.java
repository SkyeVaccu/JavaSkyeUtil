package utils;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import log.SkyeLogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.ArrayList;

/**
 * @Description 短信组件
 * @Author Skye
 * @Date 2022/11/25 11:17
 */
public class SmsUtil {

    private static final Logger logger = SkyeLogger.getLogger();

    // 短信客户端
    private SmsClient smsClient;
    // 短信请求
    private SendSmsRequest sendSmsRequest;

    /**
     * 初始化一个SmsClient
     *
     * @param secretId  密钥id
     * @param secretKey 密钥
     * @return 邮件发送对象
     */
    public SmsClient initSmsClient(String secretId, String secretKey) {
        if (StringUtils.isAnyEmpty(secretId, secretKey)) {
            logger.error("密钥Id和密钥不能为Null");
            throw new NullPointerException();
        }
        // 创建一个凭证
        Credential credential = new Credential(secretId, secretKey);
        // 创建一个短信客户端
        smsClient = new SmsClient(credential, "ap-guangzhou");
        return smsClient;
    }

    /**
     * 初始化一个短信请求
     *
     * @param sdkAppId   app的SdkId
     * @param signName   短信的签名内容
     * @param templateId 通过审核的模板Id
     * @return 发送请求
     */
    public SendSmsRequest initSendSmsRequest(String sdkAppId, String signName, String templateId) {
        if (StringUtils.isAnyEmpty(sdkAppId, signName, templateId)) {
            logger.error("appSDKId或者签名内容或者模板Id不能为Null");
            throw new NullPointerException();
        }
        // 创建一个发送请求
        sendSmsRequest = new SendSmsRequest();
        // 设置创建的短信应用id
        sendSmsRequest.setSmsSdkAppId(sdkAppId);
        // 使用短信签名内容
        sendSmsRequest.setSignName(signName);
        // 设置摸板Id
        sendSmsRequest.setTemplateId(templateId);
        // 返回包装后的返回请求
        return sendSmsRequest;
    }

    /**
     * 发送指定位数的短信验证码
     *
     * @param digest      验证码的位数
     * @param phoneNumber 需要发送的用户电话
     * @return 返回发送的验证码
     */
    public String sendVerifySms(int digest, String... phoneNumber) throws Exception {
        ArrayList<String> phoneNumberList = new ArrayList<>();
        // 默认为国内电话，添加86前缀
        for (String s : phoneNumber) {
            if (null == s || 11 == s.length()) {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.SMSPhoneNumberFormatErrorException);
            }
            phoneNumberList.add("+86" + s);
        }
        // 设置要发送的手机号
        sendSmsRequest.setPhoneNumberSet(phoneNumberList.toArray(String[]::new));
        // 生成一个6位的验证码
        String verifyCode = StringUtil.getRandomNumber(digest);
        String[] templateParamSet = new String[]{verifyCode};
        // 设置摸板参数
        sendSmsRequest.setTemplateParamSet(templateParamSet);
        // 发送验证码
        SendSmsResponse res = smsClient.SendSms(sendSmsRequest);
        SendStatus[] sendStatusSet = res.getSendStatusSet();
        for (SendStatus temp : sendStatusSet) {
            if (!temp.getCode().equals("Ok")) {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.SendSMSVerifyCodeException);
            }
        }
        return verifyCode;
    }
}
