package utils.datetime;

import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import log.SkyeLogger;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;

/** @Description 时间工具 @Author Skye @Date 2022/11/25 10:59 */
public class DateTimeUtil {

    public static final String YYYY = "yyyy";

    public static final String YYYY_MM = "yyyy-MM";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static final Logger logger = SkyeLogger.getLogger();

    /** 全局的格式化字符串,其存在默认值 */
    private static String[] globalDateFormatString = {"yyyy-MM-dd HH:mm:ss"};

    /**
     * 初始化全局时间字符串
     *
     * @param dateFormatStrings 需要使用到的时间格式字符串
     */
    public static void initGlobalDateFormatString(String... dateFormatStrings) {
        if (StringUtils.isAnyEmpty(dateFormatStrings) || ObjectUtils.isEmpty(dateFormatStrings)) {
            logger.error("传入的格式字符串不能为空或者Null");
            throw new NullPointerException();
        }
        globalDateFormatString = dateFormatStrings;
    }

    /**
     * 将日期字符串转为 yyyy-MM-dd HH:mm:ss 格式对象
     *
     * @param timeStr 日期字符串 2018-07-16 12:25:23
     * @return Data
     */
    public static Date convertStringToDate(String timeStr) {
        return convertStringToDate(timeStr, globalDateFormatString);
    }

    /**
     * 将字符串转成日期类型，允许用户传入多个格式字符串，按照顺序依次 转换，直到找到第一个成功的为止
     *
     * @param timeStr 时间字符串
     * @param dateFormatString 用户传入的不定长的格式字符串
     * @return 转换后的时间对象
     */
    public static Date convertStringToDate(String timeStr, String... dateFormatString) {
        if (null == timeStr) {
            logger.error("需要转换的时间字符串为空");
            throw new NullPointerException();
        }

        // 如果用户没有传入，则使用全局的部分
        if (StringUtils.isAnyEmpty(dateFormatString) || ObjectUtils.isEmpty(dateFormatString)) {
            dateFormatString = globalDateFormatString;
        }
        Date resultDate = null;
        // 按照顺序进行转换
        for (String temp : dateFormatString) {
            try {
                resultDate = new SimpleDateFormat(temp).parse(timeStr);
                break;
            } catch (Exception e) {
                logger.debug("当前的字符串格式错误解析错误");
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
     * @param date 日期对象
     * @param dateFormatString 格式字符串
     * @return 转换后的时间字符串
     */
    public static String convertDateToString(Date date, String... dateFormatString) {

        if (null == date) {
            logger.error("需要转换的时间字符串为空");
            throw new NullPointerException();
        }

        if (StringUtils.isAnyEmpty(dateFormatString) || ObjectUtils.isEmpty(dateFormatString)) {
            dateFormatString = globalDateFormatString;
        }

        // 按照格式进行转换
        String dateString = null;

        for (String temp : dateFormatString) {
            try {
                dateString = new SimpleDateFormat(temp).format(date);
                break;
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

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String 当前的日期
     */
    public static String dateNow() {
        return convertDateToString(new Date(), YYYY_MM_DD);
    }

    /**
     * 获取当前的完整正式时间，格式为YYYY_MM_DD_HH_MM_SS
     *
     * @return 当前的日期时间
     */
    public static String dateTimeNow() {
        return convertDateToString(new Date(), YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 获取虚拟机启动时间
     *
     * @return Date 虚拟机启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }
}
