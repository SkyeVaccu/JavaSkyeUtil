package utils.collection;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description Map方便的操作工具
 * @Author Skye
 * @Date 2022/11/25 19:32
 */
public class MapUtil {

    /**
     * 创建一个键值对的map
     *
     * @param key   键
     * @param value 值
     * @return 填充后的map
     */
    public static <K, V> Map<K, V> of(K key, V value) {
        HashMap<K, V> map = new HashMap<>(1);
        map.put(key, value);
        return map;
    }

    /**
     * 创建两个键值对的map
     *
     * @param key1   键1
     * @param value1 值1
     * @param key2   键2
     * @param value2 值2
     * @return 填充后的map
     */
    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2) {
        HashMap<K, V> map = new HashMap<>(2);
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

    /**
     * 创建三个键值对的map
     *
     * @param key1   键1
     * @param value1 值1
     * @param key2   键2
     * @param value2 值2
     * @param key3   键3
     * @param value3 值3
     * @return 填充后的map
     */
    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3) {
        HashMap<K, V> map = new HashMap<>(3);
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        return map;
    }

    /**
     * 创建四个键值对的map
     *
     * @param key1   键1
     * @param value1 值1
     * @param key2   键2
     * @param value2 值2
     * @param key3   键3
     * @param value3 值3
     * @param key4   键4
     * @param value4 值4
     * @return 填充后的map
     */
    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4) {
        HashMap<K, V> map = new HashMap<>(4);
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        return map;
    }
}
