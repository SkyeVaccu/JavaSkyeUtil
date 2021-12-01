package Utils;

import Log.SkyeLogger;
import SkyeUtilsException.SkyeUtilsExceptionType;
import SkyeUtilsException.SkyeUtilsExceptionFacoty;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

/**
 * 短信组件
 */
public class SmsUtil {

    //日志组件，用于打印对应的错误日志
    private final static Logger logger= SkyeLogger.getLogger();

    private SmsClient smsClient;
    private SendSmsRequest sendSmsRequest;

    /**
     * 初始化一个SmsClient
     * @param secretId 密钥id
     * @param secretKey 密钥
     * @return
     */
    public SmsClient initSmsClient(String secretId,String secretKey){
        if(StringUtils.isAnyEmpty(secretId,secretKey)){
            logger.error("密钥Id和密钥不能为Null");
            throw new NullPointerException();
        }
        //创建一个凭证
        Credential credential = new Credential(secretId, secretKey);
        //创建一个短信客户端
        smsClient = new SmsClient(credential,"ap-guangzhou");
        return smsClient;
    }

    /**
     * 初始化一个短信请求
     * @param sdkAppId app的SdkId
     * @param signName 短信的签名内容
     * @param templateId 通过审核的模板Id
     * @return 发送请求
     */
    public SendSmsRequest initSendSmsRequest(String sdkAppId,String signName,String templateId){
        if(StringUtils.isAnyEmpty(sdkAppId,signName,templateId)){
            logger.error("appSDKId或者签名内容或者模板Id不能为Null");
            throw new NullPointerException();
        }
        //创建一个发送请求
        sendSmsRequest = new SendSmsRequest();
        //设置创建的短信应用id
        sendSmsRequest.setSmsSdkAppId(sdkAppId);
        //使用短信签名内容
        sendSmsRequest.setSignName(signName);
        //设置摸板Id
        sendSmsRequest.setTemplateId(templateId);
        //返回包装后的返回请求
        return sendSmsRequest;
    }

    /**
     * 发送指定位数的短信验证码
     * @param digest 验证码的位数
     * @param phoneNumber 需要发送的用户电话
     * @return 返回发送的验证码
     */
    public String sendSmsVerify(int digest,String... phoneNumber) throws Exception {
        LinkedList<String> phoneNumberList = new LinkedList();
        //默认为国内电话，添加86前缀
        for (String s : phoneNumber) {
            if(null==s||11==s.length())
                throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.SMSPhoneNumberFormatErrorException);
            phoneNumberList.add( "+86"+s);
        }
        //设置要发送的手机号
        sendSmsRequest.setPhoneNumberSet(phoneNumberList.toArray(String[]::new));
        //生成一个6位的验证码
        String verifyCode=StringUtil.getRandomNumber(digest);
        String[] templateParamSet = new String[]{verifyCode};
        //设置摸板参数
        sendSmsRequest.setTemplateParamSet(templateParamSet);
        //发送验证码
        SendSmsResponse res = smsClient.SendSms(sendSmsRequest);
        SendStatus[] sendStatusSet = res.getSendStatusSet();
        for(SendStatus temp:sendStatusSet){
            if(!temp.getCode().equals("Ok"))
                throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.SendSMSVerifyCodeException);
        }
        return verifyCode;
    }
    
}
