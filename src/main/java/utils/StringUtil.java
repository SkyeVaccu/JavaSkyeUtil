package utils;

import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import log.SkyeLogger;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @Description 字符串处理工具
 * @Author Skye
 * @Date 2022/11/25 11:17
 */
public class StringUtil {

    private static final Logger logger = SkyeLogger.getLogger();

    /**
     * 判断Map中是否有指定的参数
     *
     * @param params 需要检查的Map
     * @param keys   需要存在的所有Key
     */
    public static void isKeyInMap(Map<String, Object> params, String... keys) {
        Stream.of(keys).forEach(s -> {
            if (null == params) {
                logger.error("检查的Map和键Key不能为null");
                throw new NullPointerException();
            } else if (!params.containsKey(s)) {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.LackParamException);
            }
        });
    }

    /**
     * 生成一个随机的指定位数的数字字符串
     *
     * @param digits 位数
     * @return 一个指定位数的随机数字字符串
     */
    public static String getRandomNumber(int digits) {
        Random random = new Random();
        double max = Math.pow(10, digits);
        int i = random.nextInt((int) max);
        return String.format("%0" + digits + "d", i);
    }

    /**
     * get the random str
     *
     * @param digits the digits of str
     * @return random str
     */
    public static String getRandomStr(int digits) {
        StringBuilder stringBuilder = new StringBuilder();
        String baseNumLetter = "0123456789ABCDEFGHJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < digits; i++) {
            stringBuilder.append(baseNumLetter.charAt((int) (Math.random() * baseNumLetter.length())));
        }
        return stringBuilder.toString();
    }

    /**
     * join the string array to a "a:b:c"
     *
     * @param elements the elements used to join
     * @return joined string
     */
    public static String joinRedisKey(CharSequence... elements) {
        return String.join(":", elements);
    }

    /**
     * spilt the string by the regexp , and keep the match string
     *
     * @param originString the origin string
     * @param reg          the reg
     * @return the result String array
     */
    public static String[] splitAndKeepMatchStr(String originString, String reg) {
        // create a matcher object
        Matcher matcher = Pattern.compile(reg).matcher(originString);
        // get the origin split result
        List<String> split = Arrays.asList(originString.split(reg));
        // create a new list to store the result
        List<String> strings = new LinkedList<>();
        // to traverse all match string
        int i;
        for (i = 0; matcher.find(); i++) {
            strings.add(split.get(i));
            strings.add(matcher.group());
        }
        // handle the ending character
        if (i < split.size()) {
            strings.addAll(split.subList(i, split.size()));
        }
        // filter the empty string and convert the list to array for returning
        // return array can keep the form is same as the origin split method
        return strings.stream().filter(StringUtils::isNotEmpty).toArray(String[]::new);
    }

    /**
     * a build for splitting the str now it only supports the split operation and replace operation
     */
    @Accessors(chain = true)
    @Setter
    public static class StringOperateBuilder {
        private String str;
        private boolean needStart = false;
        private String startSign;
        private boolean startSignIsStartIndexOf = true;
        private boolean needEnd = false;
        private String endSign;
        private boolean endSignIsStartIndexOf = false;

        /**
         * construct function
         *
         * @param str target str
         */
        public StringOperateBuilder(String str) {
            this.str = str;
        }

        /**
         * the function to building the target string
         *
         * @return the result String
         */
        public String build(BiFunction<Integer, Integer, String> consumer) {
            int startIndex;
            int endIndex;
            if (StringUtils.isEmpty(startSign)) {
                startIndex = startSignIsStartIndexOf ? 0 : str.length() - 1;
            } else {
                int startIndexTemp =
                        startSignIsStartIndexOf
                                ? StringUtils.indexOf(str, startSign)
                                : StringUtils.lastIndexOf(str, startSign);
                if (-1 == startIndexTemp) {
                    throw SkyeUtilsExceptionFactory.createException(
                            SkyeUtilsExceptionType.StrNotContainSignException);
                }
                startIndex = needStart ? startIndexTemp : startIndexTemp + startSign.length();
            }
            if (StringUtils.isEmpty(endSign)) {
                endIndex = endSignIsStartIndexOf ? 0 : str.length();
            } else {
                int endIndexTemp =
                        endSignIsStartIndexOf
                                ? StringUtils.indexOf(str, endSign)
                                : StringUtils.lastIndexOf(str, endSign);
                if (-1 == endIndexTemp) {
                    throw SkyeUtilsExceptionFactory.createException(
                            SkyeUtilsExceptionType.StrNotContainSignException);
                }
                endIndex = needEnd ? endIndexTemp + endSign.length() : endIndexTemp;
            }
            if (startIndex >= endIndex) {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.StartSignBehindEndSignException);
            }
            return consumer.apply(startIndex, endIndex);
        }

        /**
         * use the substring in the default situation
         *
         * @return the result str
         */
        public String build() {
            return build(str::substring);
        }
    }

    /**
     * @return a builder to split the String
     */
    public StringOperateBuilder newBuilder(String str) {
        if (StringUtils.isEmpty(str)) {
            logger.error("操作字符串为空");
            throw new RuntimeException("操作字符串为空");
        }
        return new StringOperateBuilder(str);
    }
}
