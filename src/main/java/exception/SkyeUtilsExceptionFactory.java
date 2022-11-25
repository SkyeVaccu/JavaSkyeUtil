package exception;

/**
 * @Description 异常的工厂对象
 * @Author Skye
 * @Date 2022/11/25 10:57
 */
public class SkyeUtilsExceptionFactory {

    /**
     * 传入一个枚举类型的SkyeUtilsExceptionType,然后会返回对应的SkyeUtilsException
     *
     * @param skyeUtilsExceptionType 对应的类型
     * @return 对应的错误
     */
    public static RuntimeException createException(SkyeUtilsExceptionType skyeUtilsExceptionType) {
        return skyeUtilsExceptionType.getException();
    }
}
