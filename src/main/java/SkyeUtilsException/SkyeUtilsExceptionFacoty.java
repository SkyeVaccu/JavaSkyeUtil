package SkyeUtilsException;

public class SkyeUtilsExceptionFacoty {

    /**
     * 传入一个枚举类型的SkyeUtilsExceptionType,然后会返回对应的SkyeUtilsException
     * @param skyeUtilsExceptionType 对应的类型
     * @return
     */
    public static RuntimeException createException(SkyeUtilsExceptionType skyeUtilsExceptionType){
        return skyeUtilsExceptionType.getException();
    }
}
