package utils.string;

import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.function.BiFunction;

/**
 * @Description a build for splitting the str now it only supports the split operation and replace
 * operation @Author Skye @Date 2022/11/25 20:26
 */
@Accessors(chain = true)
@Setter
public class SubstringOperator {
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
    public SubstringOperator(String str) {
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
