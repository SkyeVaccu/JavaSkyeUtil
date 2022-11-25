package utils;

import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import log.SkyeLogger;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

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

    @AllArgsConstructor
    public static class DateFilter {
        /**
         * the filter expression of the datetime which is express how to filter the date for example
         * , you can use the expression like the math expression , "this" can represent the target
         * date which need to be filter , you can use the dot sign "." to use the corresponding
         * attribute include
         * "this.year","this.month","this.day","this.hour","this.minute","this.second".
         *
         * <p>Simple example: "(this >= 2022-03-05) && this.year > 5"
         */
        private final String filterExpression;

        // define the operation key word which will appear in the filter expression
        // "&&","||" is the lower operation
        // "(" is the highest operation
        // ")" is the lowest operation
        // other sign is the normal operation
        private final List<String> allOperations = Arrays.asList("==", ">", "<", ">=", "<=", "&&", "||", "(", ")");
        private final List<String> highOperations = Arrays.asList("==", ">", "<", ">=", "<=");
        // which is used to match all operation
        private final String reg = "&&|>=|\\(|\\)|>[^=]{0}|==|<=|<[^=]{0}";
        // long string pattern
        private final Pattern longPattern = Pattern.compile("[0-9]*");

        /**
         * judge whether the dateTime can match the filterExpression
         *
         * @param dateTime the target dateTime
         * @return the result of matching
         */
        public boolean match(Date dateTime) {
            try {
                // which is store the every string part in the filter expression
                Stack<String> valueStack = new Stack<>();
                // which is store the every operation part in the filter expression
                Stack<String> operationStack = new Stack<>();
                // replace all blank string,and split the origin str according the target
                String[] stringArray = StringUtil.splitAndKeepMatchStr(filterExpression.replaceAll(" ", ""), reg);
                // pretreat all sign to the specific value
                pretreatSignValue(dateTime, stringArray);
                // traverse the string array
                for (String currentStr : stringArray) {
                    // encounter the left current str
                    if ("(".equals(currentStr)) {
                        // push the left bracket
                        operationStack.push(currentStr);
                    }
                    // we encounter the ")" in the expression
                    else if (")".equals(currentStr)) {
                        // handle the current expression
                        handleCurrentExpression(valueStack, operationStack, true, false);
                    }
                    // we encounter the operation excluding the "(",")"
                    // the first two situation has handled the "(" and ")"
                    else if (allOperations.contains(currentStr)) {
                        // top operation is higher that the current operation
                        if (operationStack.size() != 0
                                // the current operation is lower that the top operation
                                && compareOperation(operationStack.peek(), currentStr)) {
                            // handle the current expression
                            handleCurrentExpression(valueStack, operationStack, false, false);
                        }
                        // push the operation into the stack
                        operationStack.push(currentStr);
                    }
                    // we has encountered the value
                    else {
                        // put the value to the string stack
                        valueStack.push(currentStr);
                    }
                }
                // empty the stack , get the value
                return handleCurrentExpression(valueStack, operationStack, false, true);
            } catch (Exception e) {
                e.printStackTrace();
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.FilterExpressionParseException);
            }
        }

        /**
         * pretreat all signs in the expression ,we will convert it to the target value
         *
         * @param dateTime    the time is corresponded to the "this"
         * @param stringArray the string array which is need to be handle
         */
        private void pretreatSignValue(Date dateTime, String[] stringArray) {
            // traverse the array
            for (int i = 0; i < stringArray.length; i++) {
                // if we need to handle it
                if (!allOperations.contains(stringArray[i])) {
                    // convert it to the target value
                    stringArray[i] = String.valueOf(parseValueFromCallFormula(dateTime, stringArray[i]));
                }
            }
        }

        /**
         * parse the single sign to the target value
         *
         * @param dateTime    the time is corresponded to the "this"
         * @param callFormula the sign string
         * @return the long value
         */
        private long parseValueFromCallFormula(Date dateTime, String callFormula) {
            long currentComputeValue;
            // confirm the left value in the statement
            if ("this".equals(callFormula)) {
                currentComputeValue = dateTime.getTime();
            }
            // we need to get the attribute of the this
            else if (callFormula.startsWith("this.")) {
                // get the calendar object of the Date
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateTime);
                // get the corresponding value
                switch (callFormula.split("\\.")[1]) {
                    case "year":
                        currentComputeValue = calendar.get(Calendar.YEAR);
                        break;
                    case "month":
                        currentComputeValue = calendar.get(Calendar.MONTH) + 1;
                        break;
                    case "day":
                        currentComputeValue = calendar.get(Calendar.DAY_OF_MONTH);
                        break;
                    case "hour":
                        currentComputeValue = calendar.get(Calendar.HOUR_OF_DAY);
                        break;
                    case "minute":
                        currentComputeValue = calendar.get(Calendar.MINUTE);
                        break;
                    case "second":
                        currentComputeValue = calendar.get(Calendar.SECOND);
                        break;
                    default:
                        throw SkyeUtilsExceptionFactory.createException(
                                SkyeUtilsExceptionType.FilterExpressionParameterException);
                }
            } else {
                try {
                    // if it is a date str, parse it to a Date object
                    Date date = DateTimeUtil.convertStringToDate(callFormula, "yyyy-MM-dd", "yyyy-MM-dd hh:mm:ss");
                    currentComputeValue = date.getTime();
                } catch (Exception e) {
                    // if it is the long ,we need to compare it
                    currentComputeValue = Long.parseLong(callFormula);
                }
            }
            return currentComputeValue;
        }

        /**
         * judge the level between the topOperation and the current operation if the top operation
         * is higher than current operation , then return true
         *
         * @param topOperation     the operation at the top of the operation stack
         * @param currentOperation the current operation which we have encountered
         * @return judge the level, top operation is higher , then return true,otherwise return false
         */
        private boolean compareOperation(String topOperation, String currentOperation) {
            // get the top operation level
            int topOperationLevel = highOperations.contains(topOperation) ? 1 : 0;
            // get the current operation level
            int currentOperationLevel = highOperations.contains(currentOperation) ? 1 : 0;
            // if the top operation is higher than current operation , then return true
            return topOperationLevel - currentOperationLevel > 0;
        }

        /**
         * handle the Expression
         *
         * @param valueStack         the stack stored the value
         * @param operationStack     the stack stored the operation
         * @param needHandleBrackets the situation when encounter the brackets
         * @param needEmptyStack     whether empty the stack at last
         * @return the result of expression
         */
        private boolean handleCurrentExpression(Stack<String> valueStack, Stack<String> operationStack, boolean needHandleBrackets, boolean needEmptyStack) {
            int count = 0;
            for (Iterator<String> iterator = operationStack.iterator(); iterator.hasNext(); count++) {
                // we don't need to empty the stack
                if (!needEmptyStack) {
                    // 1.we have handled a expression
                    // 2.we aren't in the
                    // bracket situation
                    if ((!needHandleBrackets) && (count == 1)) {
                        break;
                    }
                    // 1. we have encounter the "("
                    // 2. we are in the bracket situation
                    if (needHandleBrackets && "(".equals(operationStack.peek())) {
                        operationStack.pop();
                        break;
                    }
                }
                // get the top operation
                String operation = operationStack.pop();
                // get the right value
                String rightValueStr = valueStack.pop();
                // get the left value
                String leftValueStr = valueStack.pop();
                // judge the type which we need to handle
                final boolean typeIsLong = longPattern.matcher(leftValueStr).matches();
                // compute the expression
                Object leftValue = typeIsLong
                        ? Long.parseLong(leftValueStr)
                        : Boolean.parseBoolean(leftValueStr);
                Object rightValue = typeIsLong
                        ? Long.parseLong(rightValueStr)
                        : Boolean.parseBoolean(rightValueStr);
                // get the result of the expression
                Boolean result = computeSingleFormula(leftValue, rightValue, operation);
                // push the value into
                valueStack.push(String.valueOf(result));
            }
            // return the value
            return Boolean.parseBoolean(valueStack.peek());
        }

        /**
         * compute the formula, it can handle the boolean formula and the long formula
         *
         * @param leftValue  the left value which is in the left of the operation
         * @param rightValue the left value which is in the left of the operation
         * @param operation  the operation
         * @return return the result
         */
        private Boolean computeSingleFormula(Object leftValue, Object rightValue, String operation) {
            switch (operation) {
                case "==":
                    return leftValue == rightValue;
                case ">=":
                    return (long) leftValue >= (long) rightValue;
                case "<=":
                    return (long) leftValue <= (long) rightValue;
                case ">":
                    return (long) leftValue > (long) rightValue;
                case "<":
                    return (long) leftValue < (long) rightValue;
                case "&&":
                    return ((boolean) leftValue) && ((boolean) rightValue);
                case "||":
                    return ((boolean) leftValue) || ((boolean) rightValue);
                default:
                    throw SkyeUtilsExceptionFactory.createException(
                            SkyeUtilsExceptionType.CanNotHandleFormulaException);
            }
        }
    }
}
