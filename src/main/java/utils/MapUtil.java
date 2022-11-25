package utils;

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
     * @param key 键
     * @param value 值
     * @return 填充后的map
     */
    public static Map<Object,Object> of(Object key,Object value){
        HashMap<Object, Object> map = new HashMap<>(1);
        map.put(key,value);
        return map;
    }

    /**
     * 创建两个键值对的map
     * @param key1 键1
     * @param value1 值1
     * @param key2 键2
     * @param value2 值2
     * @return 填充后的map
     */
    public static Map<Object,Object> of(Object key1,Object value1,Object key2,Object value2){
        HashMap<Object, Object> map = new HashMap<>(2);
        map.put(key1,value1);
        map.put(key2,value2);
        return map;
    }

    /**
     * 创建三个键值对的map
     * @param key1 键1
     * @param value1 值1
     * @param key2 键2
     * @param value2 值2
     * @param key3 键3
     * @param value3 值3
     * @return 填充后的map
     */
    public static Map<Object,Object> of(Object key1,Object value1,Object key2,Object value2,Object key3,Object value3){
        HashMap<Object, Object> map = new HashMap<>(3);
        map.put(key1,value1);
        map.put(key2,value2);
        map.put(key3,value3);
        return map;
    }

    /**
     * 创建四个键值对的map
     * @param key1 键1
     * @param value1 值1
     * @param key2 键2
     * @param value2 值2
     * @param key3 键3
     * @param value3 值3
     * @param key4 键4
     * @param value4 值4
     * @return 填充后的map
     */
    public static Map<Object,Object> of(Object key1,Object value1,Object key2,Object value2,Object key3,Object value3,Object key4,Object value4){
        HashMap<Object, Object> map = new HashMap<>(4);
        map.put(key1,value1);
        map.put(key2,value2);
        map.put(key3,value3);
        map.put(key4,value4);
        return map;
    }
}
