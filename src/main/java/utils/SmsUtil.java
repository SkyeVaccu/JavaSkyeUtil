package utils;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import log.SkyeLogger;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ObjectUtils;
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

    @Accessors(chain = true)
    @Setter
    public static class TencentSmsClientBuilder{
        // 腾讯短信服务的 密钥id
        private String secretId;
        // 腾讯短信服务的 密钥
        private String secretKey;

        public TencentSmsClient build(){
            if (StringUtils.isAnyEmpty(secretId, secretKey)) {
                logger.error("密钥Id和密钥不能为Null");
                throw new NullPointerException();
            }
            // 创建一个凭证
            Credential credential = new Credential(secretId, secretKey);
            // 创建一个短信客户端并返回
            return new TencentSmsClient(new SmsClient(credential, "ap-guangzhou"));
        }
    }

    @Accessors(chain = true)
    @Setter
    public static class SendSmsRequestBuilder{
        // app的SdkId
        private String sdkAppId;
        // 短信的签名内容
        private String signName;
        //通过审核的模板Id
        private String templateId;
        //模板对应的内容
        private String[] templateContent;
        @Setter(AccessLevel.NONE)
        @Getter
        private SendSmsRequest sendSmsRequest;

        public SendSmsRequest build(){
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
            //设置模板内容
            if(!ObjectUtils.isEmpty(templateContent)) {
                sendSmsRequest.setTemplateParamSet(templateContent);
            }
            // 返回包装后的返回请求
            return sendSmsRequest;
        }
    }

    @AllArgsConstructor
    @Getter
    public static class TencentSmsClient{
        // 短信发送平台
        private SmsClient smsClient;

        /**
         * 发送短信
         * @param sendSmsRequest 短信请求对象
         * @param phoneNumber 接收短信的手机号
         * @throws Exception 短信发送异常
         */
        public void sendSmsRequest(SendSmsRequest sendSmsRequest, String... phoneNumber) throws Exception {
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
            //发送短信
            SendSmsResponse res = smsClient.SendSms(sendSmsRequest);
            SendStatus[] sendStatusSet = res.getSendStatusSet();
            for (SendStatus temp : sendStatusSet) {
                if (!temp.getCode().equals("Ok")) {
                    throw SkyeUtilsExceptionFactory.createException(
                            SkyeUtilsExceptionType.SendSMSVerifyCodeException);
                }
            }
        }
    }
}
