package SkyeUtilsException;

/**
 * 自定义异常类的种类
 */
public enum SkyeUtilsExceptionType {

    /**
     * 用于定义所有的将会用到的异常
     */
    CreateBeanException("转换为Bean失败",10001),
    CreateBeanListException("转换为Bean列表失败",10002),
    GetObjectJsonException("获得对象对应Json失败",10003),
    LackAppropriateDateFormatException("缺少合适的时间格式",10004),
    CompressImageException("压缩文件失败",10005),
    FileIsNotImageException("文件不是图片",10006),
    MD5EncodeException("MD5加密失败",10007),
    LackParamException("Map中缺少指定的参数",10008),
    StrNotContainSignException("字符串不包含指定的字符",10009),
    StartSignBehindEndSignException("起始标志晚于结束标志",10010),
    LackEncodeInfoException("缺少加密的信息",10011),
    DecodeStrException("解密字符串失败",10012),
    NotExistDesignatedDataException("不存在指定的数据",10013),
    LackDecodeInfoException("缺少解密的信息",10014),
    SendSMSVerifyCodeException("发送短信验证码错误",10015),
    SMSPhoneNumberFormatErrorException("需要发送验证码的手机号格式错误",10016),
    CompressRatioIsTooSmallException("压缩率太小",10017),
    RequestPathAndParamsErrorException("请求路径不能为空或请求参数类型错误",10018),
    EmailPropertiesErrorException("邮件配置信息错误",10019),
    MessageSubjectAndCotentErrorException("邮件的主题和内容不能为空",10010),
    EmailIsIllegalException("收件人的地址非法",10011),
    EmailNotInitException("邮件配置没有初始化",10012),
    VerificationImageSizeErrorException("验证码图片的尺寸不合法",10013),
    VerificationRandomLineNumErrorException("验证码干扰线数量错误",10014),
    VerificationRandomStrNumErrorException("验证码字符数量错误",10015),
    CreateVerificationImageErrorException("生成验证码图片失败",10016),
    ChangeFileNameErrorException("修改文件名错误",10017),
    LackNewFilePathException("缺少文件新途径",10018),
    NotExistStringOperateException("不存在的字符串操作",10019),
    LackOldFilePathException("缺少文件旧途径",10020),
    FileInfoConflictException("文件信息冲突",10021),
    ;


    private SkyeUtilsException exception;

    SkyeUtilsExceptionType(String message, int errorCode) {
        this.exception=new SkyeUtilsException(message,errorCode);
    }

    protected SkyeUtilsException getException() {
        return exception;
    }
}
