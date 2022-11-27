package utils.sms;

import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import log.SkyeLogger;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

/**
 * @Description 腾讯短信请求 @Author Skye @Date 2022/11/25 20:08
 */
@AllArgsConstructor
@Getter
public class TencentSmsRequest {
    private static final Logger logger = SkyeLogger.getLogger();

    @Setter(AccessLevel.NONE)
    private SendSmsRequest sendSmsRequest;

    @Accessors(chain = true)
    @Setter
    public static class TencentSmsRequestBuilder {
        // app的SdkId
        private String sdkAppId;
        // 短信的签名内容
        private String signName;
        // 通过审核的模板Id
        private String templateId;
        // 模板对应的内容
        private String[] templateContent;

        public TencentSmsRequest build() {
            if (StringUtils.isAnyEmpty(sdkAppId, signName, templateId)) {
                logger.error("appSDKId或者签名内容或者模板Id不能为Null");
                throw new NullPointerException();
            }
            // 创建一个发送请求
            SendSmsRequest sendSmsRequest = new SendSmsRequest();
            // 设置创建的短信应用id
            sendSmsRequest.setSmsSdkAppId(sdkAppId);
            // 使用短信签名内容
            sendSmsRequest.setSignName(signName);
            // 设置摸板Id
            sendSmsRequest.setTemplateId(templateId);
            // 设置模板内容
            if (!ObjectUtils.isEmpty(templateContent)) {
                sendSmsRequest.setTemplateParamSet(templateContent);
            }
            // 返回包装后的返回请求
            return new TencentSmsRequest(sendSmsRequest);
        }
    }
}
