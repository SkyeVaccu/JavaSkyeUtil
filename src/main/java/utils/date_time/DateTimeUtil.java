package utils.date_time;

import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import log.SkyeLogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description 时间工具
 * @Author Skye
 * @Date 2022/11/25 10:59
 */
public class DateTimeUtil {

    private static final Logger logger = SkyeLogger.getLogger();

    /**
     * 全局的格式化字符串,其存在默认值
     */
    private static String[] globalDateFormatString = {"yyyy-MM-dd hh:mm:ss"};

    /**
     * 初始化全局时间字符串
     *
     * @param dateFormatStrings 需要使用到的时间格式字符串
     */
    public static void initGlobalDateFormatString(String... dateFormatStrings) {
        if (StringUtils.isAnyEmpty(dateFormatStrings)) {
            logger.error("传入的格式字符串不能为空或者Null");
            throw new NullPointerException();
        }
        globalDateFormatString = dateFormatStrings;
    }

    /**
     * 将字符串转成日期类型，允许用户传入多个格式字符串，按照顺序依次 转换，直到找到第一个成功的为止
     *
     * @param timeStr          时间字符串
     * @param dateFormatString 用户传入的不定长的格式字符串
     * @return 转换后的时间对象
     */
    public static Date convertStringToDate(String timeStr, String... dateFormatString) {
        // 如果用户没有传入，则使用全局的部分
        if (StringUtils.isAnyEmpty(dateFormatString)) {
            dateFormatString = globalDateFormatString;
        }
        // 判断时间字符串是否为空
        if (null == timeStr) {
            logger.error("需要转换的时间字符串为空");
            // 抛出空指针异常
            throw new NullPointerException();
        }
        Date resultDate = null;
        // 按照顺序进行转换
        for (String temp : dateFormatString) {
            try {
                DateFormat dateFormat = new SimpleDateFormat(temp);
                resultDate = dateFormat.parse(timeStr);
                break;
            } catch (ParseException e) {
                // 解析异常，则直接跳过，然后解析下一个
                logger.debug("当前的字符串格式错误转换错误");
            }
        }
        // 转换失败
        if (null == resultDate) {
            throw SkyeUtilsExceptionFactory.createException(
                    SkyeUtilsExceptionType.LackAppropriateDateFormatException);
        }
        return resultDate;
    }

    /**
     * 将对应的日期对象按照格式转化为字符串，允许多个格式字符串，依次找到第一个合适的结束
     *
     * @param date             日期对象
     * @param dateFormatString 格式字符串
     * @return 转换后的时间字符串
     */
    public static String convertDateToString(Date date, String... dateFormatString) {
        if (StringUtils.isAnyEmpty(dateFormatString)) {
            dateFormatString = globalDateFormatString;
        }
        // 判断日期是否为null
        if (null == date) {
            logger.error("需要转换的时间字符串为空");
            throw new NullPointerException();
        }
        // 按照格式进行转换
        String dateString = null;
        for (String temp : dateFormatString) {
            try {
                DateFormat dateFormat = new SimpleDateFormat(temp);
                // use the UTC
                dateString = dateFormat.format(date);
            } catch (Exception e) {
                logger.debug("当前的字符串格式错误转换错误");
            }
        }
        if (null == dateString) {
            throw SkyeUtilsExceptionFactory.createException(
                    SkyeUtilsExceptionType.LackAppropriateDateFormatException);
        }
        return dateString;
    }
}
