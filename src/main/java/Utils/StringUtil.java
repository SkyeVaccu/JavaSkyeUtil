package Utils;

import Log.SkyeLogger;
import SkyeUtilsException.SkyeUtilsExceptionFacoty;
import SkyeUtilsException.SkyeUtilsExceptionType;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 字符串处理工具
 */
public class StringUtil {

    //日志组件，用于打印对应的错误日志
    private final static Logger logger= SkyeLogger.getLogger();

    /**
     * 判断Map中是否有指定的参数
     * @param params 需要检查的Map
     * @param keys 需要存在的所有Key
     * @throws Exception 缺少参数
     */
    public static void isKeyInMap(Map params, String... keys) throws Exception {
        Stream.of(keys).forEach(s->{
            if(null==params||null==keys) {
                logger.error("检查的Map和键Key不能为null");
                throw new NullPointerException();
            }
            else if(!params.containsKey(s))
                throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.LackParamException);
        });
    }

    /**
     * 生成一个随机的指定位数的数字字符串
     * @param digits 位数
     * @return 一个指定位数的随机数字字符串
     */
    public static String getRandomNumber(int digits){
        Random random = new Random();
        Double max=Math.pow(10,digits);
        int i = random.nextInt(max.intValue());
        return String.format("%0" + digits + "d", i);
    }

    /**
     * get the randomstr
     * @param digits the digits of str
     * @return randomstr
     */
    public static String getRandomStr(int digits){
        StringBuffer stringBuffer = new StringBuffer();
        String baseNumLetter = "0123456789ABCDEFGHJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < digits; i++) {
            stringBuffer.append(baseNumLetter.charAt((int)(Math.random()*baseNumLetter.length())));
        }
        return stringBuffer.toString();
    }

    @Accessors(chain = true)
    @Setter
    /**
     * a build for spliting the str
     * now it only supports the split operation and replace operation
     */
    class StringOperateBuilder{
        private String str;
        private boolean needStart=false;
        private String startSign;
        private boolean startSignIsStartIndexOf=true;
        private boolean needEnd=false;
        private String endSign;
        private boolean endSignIsStartIndexOf=false;

        /**
         * construct function
         * @param str  target str
         */
        public StringOperateBuilder(String str){
            this.str=str;
        }

        /**
         * the function to building the target string
         * @return the result String
         */
        public String build(BiFunction<Integer,Integer,String> consumer){
            int startIndex=0;
            int endIndex=0;
            if (StringUtils.isEmpty(startSign))
                startIndex=startSignIsStartIndexOf?0:str.length()-1;
            else {
                int startIndexTemp = startSignIsStartIndexOf ? StringUtils.indexOf(str,startSign) : StringUtils.lastIndexOf(str,startSign);
                if(-1==startIndexTemp)
                    throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.StrNotContainSignException);
                startIndex=needStart?startIndexTemp:startIndexTemp+startSign.length();
            }
            if (StringUtils.isEmpty(endSign))
                startIndex=endSignIsStartIndexOf?0:str.length()-1;
            else{
                int endIndexTemp = endSignIsStartIndexOf ? StringUtils.indexOf(str,endSign) : StringUtils.lastIndexOf(str,endSign);
                if(-1==endIndexTemp)
                    throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.StrNotContainSignException);
                endIndex=needEnd?endIndexTemp+endSign.length():endIndexTemp;
            }
            if(startIndex >= endIndex)
                throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.StartSignBehindEndSignException);
            return consumer.apply(startIndex, endIndex);
        }
    }

    /**
     * @return a builder to split the String
     */
    public StringOperateBuilder newBuilder(String str){
        return new StringOperateBuilder(str);
    }



}
