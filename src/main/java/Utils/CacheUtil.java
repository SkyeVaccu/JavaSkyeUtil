package Utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * use the locale to store object
 */
public class CacheUtil {

    /**
     * the local cache container
     */
    private static Map<String,Object> cache=new ConcurrentHashMap<>();

    /**
     * a Thread pool whose size is 6,it is for removing the data in the cache
     */
    private static ScheduledExecutorService scheduledExecutorService=Executors.newScheduledThreadPool(6);


    public static void put(String key,Object value){
        cache.put(key,value);
    }

    public static void put(String key, Object value, TimeUnit timeUnit,long time){
        cache.put(key,value);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                cache.remove(key,value);
            }
        }, time, timeUnit);
    }

    public static Object get(String key){
        return cache.get(key);
    }
}
