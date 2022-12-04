package exception;

/**
 * @Description 自定义异常类的种类 @Author Skye @Date 2022/11/25 10:58
 */
public enum SkyeUtilsExceptionType {

    /** 用于定义所有的将会用到的异常 */
    CreateBeanException("转换为Bean失败", 10001),
    CreateBeanListException("转换为Bean列表失败", 10002),
    GetObjectJsonException("获得对象对应Json失败", 10003),
    LackAppropriateDateFormatException("缺少合适的时间格式", 10004),
    CompressImageException("压缩文件失败", 10005),
    FileIsNotImageException("文件不是图片", 10006),
    MD5EncodeException("MD5加密失败", 10007),
    LackParamException("Map中缺少指定的参数", 10008),
    StrNotContainSignException("字符串不包含指定的字符", 10009),
    StartSignBehindEndSignException("起始标志晚于结束标志", 10010),
    LackEncodeInfoException("缺少加密的信息", 10011),
    DecodeStrException("解密字符串失败", 10012),
    NotExistDesignatedDataException("不存在指定的数据", 10013),
    LackDecodeInfoException("缺少解密的信息", 10014),
    SendSMSVerifyCodeException("发送短信验证码错误", 10015),
    SMSPhoneNumberFormatErrorException("需要发送验证码的手机号格式错误", 10016),
    CompressRatioIsTooSmallException("压缩率太小", 10017),
    RequestPathAndParamsErrorException("请求路径不能为空或请求参数类型错误", 10018),
    EmailPropertiesErrorException("邮件配置信息错误", 10019),
    MessageSubjectAndCotentErrorException("邮件的主题和内容不能为空", 10010),
    EmailIsIllegalException("收件人的地址非法", 10011),
    EmailNotInitException("邮件配置没有初始化", 10012),
    VerificationImageSizeErrorException("验证码图片的尺寸不合法", 10013),
    VerificationRandomLineNumErrorException("验证码干扰线数量错误", 10014),
    VerificationRandomStrNumErrorException("验证码字符数量错误", 10015),
    CreateVerificationImageErrorException("生成验证码图片失败", 10016),
    ChangeFileNameErrorException("修改文件名错误", 10017),
    LackNewFilePathException("缺少文件新途径", 10018),
    NotExistStringOperateException("不存在的字符串操作", 10019),
    LackOldFilePathException("缺少文件旧途径", 10020),
    FileInfoConflictException("文件信息冲突", 10021),
    UserLockedException("账户被锁定", 10022),
    CredentialsExpiredException("密码已过期", 10023),
    UserExpiredException("账户已过期", 10024),
    UserBanedException("账户被禁用", 10025),
    BadCredentialsException("用户名或者密码输入错误", 10026),
    NotAuthenticationException("当前用户未认证", 10027),
    ControllerAopException("切面方法操作错误", 10028),
    HasSentSMSBeforeException("规定时间内已经发送过短信", 10029),
    VerificationCodeErrorException("验证码不存在或者错误", 10030),
    ValidateErrorException("缺少数据或数据错误", 10031),
    EpicGameUrlParseException("Epic游戏解析错误", 10032),
    FilterExpressionParameterException("时间过滤表达式参数调用错误", 10033),
    FilterExpressionComputeErrorException("时间过滤表达式计算错误", 10034),
    CanNotHandleFormulaException("无法处理该计算表达式", 10035),
    FilterExpressionParseException("时间过滤表达式计息异常", 10036),
    EsSearchByConditionException("条件检索错误", 10037),
    EsSearchDeserializeErrorException("ES反序列化错误", 10038),
    HandleTextWebSocketMessageErrorException("处理文本WebSocket消息失败", 10039),
    DoNotSupportWebSocketBinaryException("不支持处理websocket二进制信息", 10040),
    DoNotSupportTheOriginWebSocketTypeException("不支持的WebSocket原生类型", 10041),
    DoNotSupportWebSocketMessageTypeException("不支持处理改WebSocket自定义类型", 10042),
    WebSocketSessionNotExistException("Session没被保存", 10043),
    WebSocketClientNoResponseException("WebSocket客户端未响应", 10045),
    CanNotParseLoginRequestException("无法解析登录请求", 10046),
    FileUploadErrorException("文件上传失败", 10047),
    UserNotFoundException("该用户不存在", 10048),
    EpicGameNotFoundException("该Epic游戏不存在", 10049),
    NewCanNotWithdrawException("该消息超过退回期限或不存在", 10050),
    PrivateChatWindowNotExistException("私聊窗口不存在", 10051),
    LogoutErrorException("注销账号失败", 10052),
    RetryFunctionIsEmptyException("重试方法为空", 10053),
    WebSocketSendNewErrorException("WebSocketSendNew类型信息处理错误", 10054),
    WebSocketServerOpenEventException("WebSocket服务器打开事件异常", 10055),
    WebSocketServerCloseEventException("WebSocket服务器关闭事件异常", 10056),
    WebSocketServerMessageEventException("WebSocket服务器处理事件异常", 10057),
    WebSocketServerErrorEventException("WebSocket服务器错误事件异常", 10058),
    WebSocketServerStartEventException("WebSocket服务器启动事件异常", 10059),
    TableRowNotExistException("Table行不存在错误", 10060),
    TableColumnNotExistException("Table列不存在错误", 10061),
    WebSocketConnectionCloseException("WebSocket连接已关闭", 10062),
    WebSocketClientParamErrorException("WebSocket客户端参数不能为为空", 10063),
    WebSocketServerParamErrorException("WebSocket服务端参数不能为为空", 10064),
    CanNotFindClassException("找不到对应的数据类型", 10065),
    RemoteCallClientNotExistException("远程调用平台不存在", 10066),
    RemoteMethodCallErrorException("远程方法调用错误", 10067),
    CanNotFindEnumConstantException("无法找到枚举常量", 10068),
    ConvertPrimitiveDataErrorException("转换基本数据类型错误", 10069),
    RMIFutureTaskNotExistException("RMI结果获取FutureTask不存在", 10070),
    RMIRequestWrapperNotExistException("RMI结果获取请求包不存在", 10071),
    CreateProtobufBeanException("创造Protobuf对象错误", 10072),
    ;

    private final SkyeUtilsException exception;

    SkyeUtilsExceptionType(String message, int errorCode) {
        this.exception = new SkyeUtilsException(message, errorCode);
    }

    SkyeUtilsException getException() {
        return exception;
    }
}
