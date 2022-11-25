package utils.sms;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import log.SkyeLogger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.ArrayList;

/**
 * @Description 腾讯短信请求
 * @Author Skye
 * @Date 2022/11/25 20:05
 */
@AllArgsConstructor
@Getter
public class TencentSmsClient {
    private static final Logger logger = SkyeLogger.getLogger();

    // 短信发送平台
    private SmsClient smsClient;

    /**
     * 发送短信
     *
     * @param tencentSmsRequest 腾讯短信请求对象
     * @param phoneNumber       接收短信的手机号
     * @throws Exception 短信发送异常
     */
    public void sendSmsRequest(TencentSmsRequest tencentSmsRequest, String... phoneNumber) throws Exception {
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
        tencentSmsRequest.getSendSmsRequest().setPhoneNumberSet(phoneNumberList.toArray(String[]::new));
        //发送短信
        SendSmsResponse res = smsClient.SendSms(tencentSmsRequest.getSendSmsRequest());
        SendStatus[] sendStatusSet = res.getSendStatusSet();
        for (SendStatus temp : sendStatusSet) {
            if (!"Ok".equals(temp.getCode())) {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.SendSMSVerifyCodeException);
            }
        }
    }

    @Accessors(chain = true)
    @Setter
    public static class TencentSmsClientBuilder {
        // 腾讯短信服务的 密钥id
        private String secretId;
        // 腾讯短信服务的 密钥
        private String secretKey;

        public TencentSmsClient build() {
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
}
