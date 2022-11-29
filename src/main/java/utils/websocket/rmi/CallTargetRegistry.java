package utils.websocket.rmi;

import utils.collection.Table;

/**
 * @Description 调用对象注册表，通过key以及接口对象，就可以服务端已经注册的对象 @Author Skye @Date 2022/11/28 18:49
 */
public class CallTargetRegistry {
    // 用于存储调用对象的二维表格
    public static final Table<String, Class<?>, Object> CALL_TARGET_TABLE = new Table<>();

    /**
     * 获得调用的目标
     *
     * @param callTargetKey 调用对象key
     * @param clazz 调用类的class
     * @return 已经注册的对象
     * @param <T> 目标类
     */
    public static <T> T find(String callTargetKey, Class<T> clazz) {
        return (T) CALL_TARGET_TABLE.get(callTargetKey, clazz);
    }

    /**
     * 注册一个对象
     *
     * @param classTargetKey 调用对象key
     * @param clazz 接口类的class
     * @param obj 具体实现类
     * @param <T> 实现类
     */
    public static <T> void register(String classTargetKey, Class<? super T> clazz, T obj) {
        CALL_TARGET_TABLE.put(classTargetKey, clazz, obj);
    }
}
