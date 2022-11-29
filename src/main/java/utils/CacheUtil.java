package utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Description use the locale to store object @Author Skye @Date 2022/11/25 10:58
 */
public class CacheUtil {

    /** the local cache container */
    private static final Map<String, Object> CACHE = new ConcurrentHashMap<>();

    /**
     * 放入某个数据
     *
     * @param key 键
     * @param value 值
     */
    public static <T> void put(String key, T value) {
        CACHE.put(key, value);
    }

    /**
     * 获得某个数据
     *
     * @param key 键
     */
    public static <T> T get(String key) {
        return (T) CACHE.get(key);
    }

    /**
     * 删除某个数据
     *
     * @param key 键
     */
    public static void remove(String key) {
        CACHE.remove(key);
    }

    /**
     * 放入信息后，延迟一定时间删除
     *
     * @param key 对应的Key
     * @param value 对应的值
     * @param timeUnit 时间单位
     * @param time 时间
     */
    public static <T> void put(String key, T value, TimeUnit timeUnit, long time) {
        CACHE.put(key, value);
        // 提交一个延期任务
        AsyncUtil.submitTaskDelayed(
                () -> {
                    CACHE.remove(key, value);
                },
                time,
                timeUnit);
    }
}
