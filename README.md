## 介绍

一个简单的工具类，基于部分功能基于JDK11，使用Gradle进行构建，如果需要Maven版本，自行参照gradle文件进行修改  
这个工具类包含有常规的字符串处理等功能，同时提供HTTP,websocket,mail,sms等服务，对基本框架的封装，适合新手使用



### 日志工具 SkyeLogger

能够简单地获得日志对象，并且打印日志

```java
Logger logger=SkyeLogger.getLogger();
logger.info("this is a log");
```



### 缓存工具 CacheUtil

使用Map来当作内存中的全局缓存使用

**存入缓存**

向缓存追加内容

```java
CacheUtil.put("key","value");
```

**移除缓存**

移除缓存内容

```java
CacheUtil.remove("key");
```

**定时缓存**

指定放入内容的时间，到达事件后将会被删除

```java
CacheUtil.put("key1","value1",TimeUnit.SECONDS,3);
```



### 文件工具 FileUtil

**修改文件的名字**

修改文件名，"D:/test.png"--->"D:/test2.png"

```java
FileUtil.changeFileName(new File("D:/test.png"),"test2");
```

**获得文件的拓展名**

```java
FileUtil.getFileExtensionName("D:/test.png")
```



### Http工具 HttpUtil

**Get请求**

发起一个Get请求

```java
HttpResponse<String> response=HttpUtil.getRequest(
        "https://www.baidu.com",
        MapUtil.of("searchContent","testSearch"));
        System.out.println(response);
```

**POST请求**

发起一个POST请求

```java
HttpResponse<String> response=HttpUtil.postRequest(
        "https://www.baidu.com",
        MapUtil.of("searchContent","testSearch"));
        System.out.println(response);
```



### 集合工具 MapUtil

**快速创建一个Map**

```java
MapUtil.of("key","value");
MapUtil.of("key1","value1","key2","value2");
MapUtil.of("key1","value1","key2","value2","key3","value3");
MapUtil.of("key1","value1","key2","value2","key3","value3","key4","value4");
```

**判断一系列的key是否在Map中**

```java
MapUtil.isKeyInMap(map,"key");
```



### MD5工具 Md5Util

**加密**

对字符串进行MD5加密，获得对应的加密值

```java
Md5Util.getMd5String("123456");
```



### 序列化工具 SerializeUtil

对jackson进行一定程度的封装，其他的转换见类文件

```java
SerializeUtil.convertJsonToBeanByClass("{\"name\":"123"}",Username.class);
```



### Table

参照guava的table,二维Map

```java
// 由row,column确定value
Table<String, String,int>table = new Table();
```



### 数据类型转换器 Convert

**Object 转 Integer**

Object 转 Integer，并设置转换失败默认值

```java
Integer integer = Convert.toInt(123, 0);
System.out.print(integer);
```

**Object 转 String**

Object 转 String，并设置转换失败默认值

```java
String str = Convert.toStr(123, "");
System.out.print(str);
```



### 时间工具 DateTimeUtil

自带默认时间转换格式`yyyy-MM-dd HH:mm:ss`



**初始化**

初始化一些待定的时间格式

```java
DateTimeUtil.initGlobalDateFormatString("yyyy-MM-dd","yyyy-MM-dd hh:mm:ss","yyyy-MM-dd hh");
```



**字符串转换Date**

将时间从字符串转换成Date对象，如果没有转入对应的格式字符串会调用默认的

```java
DateTimeUtil.convertStringToDate("2022-11-27 10");
```

**Date转换为字符串**

将时间转换为字符串，如果没有转入对应的格式字符串会调用默认的

```java
DateTimeUtil.convertDateToString(new Date());
```



**获取虚拟机启动时间**

```java
// 获取当前虚拟机启动时间
DateTimeUtil.getServerStartDate();
System.out.println(DateTimeUtil.convertDateToString(serverStartDate));  // 2022-12-26 10:53:55
```

**获取当前日期**

```java
String dateStr = DateTimeUtil.dateNow();
System.out.println(dateStr);    // 2022-12-26
```



**获取当前日期时间**

```java
String dateStr = DateTimeUtil.dateTimeNow();
System.out.println(dateStr);    // 2022-12-26 11:12:35
```

### 时间判断器 DateFilter

判断传入的时间是否符合表达式

```java
//创建一个时间表达式
DateFilter dateFilter1=new DateFilter("this.year==2022 && this.month==11 && this.day==27");
//对时间进行匹配
dateFilter1.match(new Date());

DateFilter dateFilter3=new DateFilter("this.year==2023 || (this.month==11 && this.day==27)");
dateFilter3.match(new Date());
```



### 图像处理工具 ImageUtil

**判断图像是否是照片**

```java
ImageUtil.isImage("C:/test.png");
```



**将路径修改为jpg结尾**

```java
ImageUtil.convertImagePathToJpgSuffix("C:/test.png");
```



### 图像压缩器

将图像的质量进行压缩，减小大小，默认压缩倍率0.3

```java
File file=new File("C:/test.png");
        new ImageCompressor()
        .setCompressRatio(0.2f)
        .setFile(file)
        .setNewPath("C:/test1.png")
        .compress();
File file1=new File("C:/test1.png");
assert file1.length()<file.length();
```



### jwt工具 JwtUtil

可以快速地生成和解码jwt

```java
//编码jwt
String encode=JwtUtil.encode(
        EncodeOrigin.getDefaultEncodeOrigin(),
        MapUtil.of("key","value"));
//解码jwt
DecodeInfo decodeInfo=JwtUtil.decode(
        encode,
        DecodeOrigin.getDefaultEncodeOrigin(),
        Map.of("key",String.class));
```


### 邮件工具 MailUtil

**正则校验**

判断String字符串是否是邮件格式

```java
MailUtil.isMailLegal("123456789@163.com");
```

 

**发送邮件**

```java
// 创建一个邮件会话
MailSession mailSession =new MailSession.MailSessionBuilder()
        .setUsername("发送邮箱")
        .setPassword("发送邮箱的密码")
        .build();

// 创建一条邮件信息
SimpleMailMessage simpleMailMessage =new SimpleMailMessage.SimpleMailMessageBuilder()
        .setMails(new String[] {"目标邮箱"})
        .setContent("测试邮件")
        .setSubject("测试主题")
        .setMailSession(mailSession)
        .build();

// 发送邮件
mailSession.sendMail(simpleMailMessage);
```



### 腾讯短信工具  TencentSmsClient

**正则校验**

判断String字符串是否是手机号形式

```java
SmsUtil.isPhoneNumberLegal("12345678901");
```



**简单接入腾讯短信平台**

```java
//创建腾讯短信平台
TencentSmsClient tencentSmsClient =new TencentSmsClient.TencentSmsClientBuilder()
        .setSecretId("SecretId")
        .setSecretKey("SecretKey")
        .build();
//创建一条短信
TencentSmsRequest tencentSmsRequest =new TencentSmsRequest.TencentSmsRequestBuilder()
        .setSdkAppId("SdkAppId")
        .setSignName("SignName")
        .setTemplateId("TemplateId")
        .setTemplateContent(new String[] {"content"})
        .build();
//发送短信
tencentSmsClient.sendSmsRequest(tencentSmsRequest, "targetPhoneNumber");
```


### 字符串工具 StringUtil

**生成指定位数的数字**

```java
StringUtil.getRandomNumber(6)
```


**生成指定位数的字符串**

```java
StringUtil.getRandomStr(6)
```


**分割字符串保留分隔符**

```java
String[] split = "http://www.baidu.com".split("\\.");
StringUtil.splitAndKeepMatchStr("http://www.baidu.com", "\\.");
```


### 字符串截取器 SubstringOperator

```java
//将会获得 www.baidu
new SubstringOperator("http://www.baidu.com")
                .setStartSign("//")
                .setEndSign(".")
                .build();
```


### 验证码工具 ImageVerification

```java
//生成一个图片验证码
ImageVerification imageVerification = new ImageVerification.ImageVerificationBuilder().build();
```


### 任务工具 AsyncUtil

**提交一个立刻执行的任务**

```java
AsyncUtil.submitTask(
                () -> {
                    System.out.println("aaa");
                });
```



**提交一个延期执行的任务**

```java
AsyncUtil.submitTaskDelayed(
        () -> {
            assert System.currentTimeMillis() - startTime > 3000;
        },
        3,
        TimeUnit.SECONDS);
```



**提交一个周期执行的任务**

```java
AsyncUtil.submitTaskPeriod(
        () -> {
            long tempTime = System.currentTimeMillis();
            assert tempTime - startTime1[0] > 3000;
            startTime1[0] = tempTime;
        },
        3,
        3,
        TimeUnit.SECONDS,
        3);
```


### WebSocket工具 WebSocketServer

**创建一个服务端**

```java
WebSocketServer webSocketServer =new WebSocketServer.WebSocketServerBuilder()
                        .setHost("127.0.0.1")
                        .setPort("8888")
                        .build();

// 注册接收者和发送者
TestServerReceiver webSocketServerReceiver = new TestServerReceiver();
TestServerSender webSocketServerSender = new TestServerSender();
webSocketServer.register(webSocketServerReceiver);
webSocketServer.register(webSocketServerSender);

// 启动服务器
webSocketServer.start();
```


**创建一个客户端**

```java
WebSocketClient webSocketClient =
                new WebSocketClient.WebSocketClientBuilder()
                        .setHost("127.0.0.1")
                        .setPort("8888")
                        .build();

// 注册接收者和发送者
TestClientReceiver webSocketClientReceiver = new TestClientReceiver();
TestClientSender webSocketClientSender = new TestClientSender();
webSocketClient.register(webSocketClientReceiver);
webSocketClient.register(webSocketClientSender);

// 连接客户端
webSocketClient.connect();
```


### 远程方法调用工具

基于WebSocket工具，实现对于跨网络的方法调用，如果为可连通网络，建议使用RMI而非本工具  
```java
// 创建一个webSocket服务器
WebSocketServer webSocketServer =
        new WebSocketServer.WebSocketServerBuilder()
                .setHost("127.0.0.1")
                .setPort("8888")
                .build();
RmiCalleeServerReceiver rmiCalleeServerReceiver = new RmiCalleeServerReceiver("server");
webSocketServer.register(rmiCalleeServerReceiver);
webSocketServer.start();

// 客户端调用者
WebSocketClient webSocketClientCaller =
        new WebSocketClient.WebSocketClientBuilder()
                .setHost("127.0.0.1")
                .setPort("8888")
                .build();
RmiCallerClientReceiver rmiCallerClientReceiver = new RmiCallerClientReceiver();
webSocketClientCaller.register(rmiCallerClientReceiver);
webSocketClientCaller.connect();

// 客户端被调者
WebSocketClient webSocketClientCallee =
        new WebSocketClient.WebSocketClientBuilder()
                .setHost("127.0.0.1")
                .setPort("8888")
                .addOpenParams(WebSocketClient.RMI_CALLEE_END_KEY, "dog")
                .build();
RmiCalleeClientReceiver rmiCalleeClientReceiver = new RmiCalleeClientReceiver();
webSocketClientCallee.register(rmiCalleeClientReceiver);
webSocketClientCallee.connect();

// 提交一个调用任务
AsyncUtil.submitTaskDelayed(
        () -> {
            DogInterface dogInterface =
                    ReflectUtil.createRemoteProxy(
                            webSocketClientCaller, DogInterface.class, "dog", "dog");
            int call = dogInterface.call(10);
            System.out.println("本地成功调用----" + call);
        },
        3,
        TimeUnit.SECONDS);

// 注册一个对象
CallTargetRegistry.register("dog", DogInterface.class, new Dog());
```

