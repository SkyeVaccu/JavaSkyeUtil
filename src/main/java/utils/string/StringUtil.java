package utils.string;

import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import log.SkyeLogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.*;
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
}
