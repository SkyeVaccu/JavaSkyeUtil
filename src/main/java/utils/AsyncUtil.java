package utils;

import org.apache.commons.lang3.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Description 异常任务的线程 @Author Skye @Date 2022/11/26 23:11
 */
public class AsyncUtil {

    // 线程池对象
    @SuppressWarnings("AlibabaThreadPoolCreation")
    private static final ScheduledExecutorService scheduledExecutorService =
            Executors.newScheduledThreadPool(6);

    // 记录次数的Map
    private static final Map<ScheduledFuture<?>, Integer> TIME_MAP = new HashMap<>();

    /**
     * 执行目标任务
     *
     * @param runnable 任务对象
     */
    public static void submitTask(Runnable runnable) {
        scheduledExecutorService.execute(runnable);
    }

    /**
     * 延期执行一个任务
     *
     * @param runnable 任务对象
     * @param delayedTime 延迟的时间
     * @param timeUnit 时间单位
     */
    public static void submitTaskDelayed(Runnable runnable, long delayedTime, TimeUnit timeUnit) {
        scheduledExecutorService.schedule(runnable, delayedTime, timeUnit);
    }

    /**
     * 周期性执行任务
     *
     * @param runnable 任务对象
     * @param delayedTime 初次执行的延迟时间
     * @param periodTime 周期间隔时间
     * @param timeUnit 时间单位
     * @param time 执行的次数
     */
    public static void submitTaskPeriod(
            Runnable runnable, long delayedTime, long periodTime, TimeUnit timeUnit, Integer time) {
        ScheduledFuture<?>[] scheduledFutures = new ScheduledFuture[1];
        ScheduledFuture<?> scheduledFuture =
                scheduledExecutorService.scheduleAtFixedRate(
                        () -> {
                            ScheduledFuture<?> currentScheduledFuture = scheduledFutures[0];
                            // 获得当前执行的次数
                            Integer count = TIME_MAP.get(currentScheduledFuture);
                            // 如果为空，则新增一个
                            if (ObjectUtils.isEmpty(count)) {
                                count = 0;
                                TIME_MAP.put(currentScheduledFuture, count);
                            }
                            // 调用目标函数
                            runnable.run();
                            // 次数+1
                            count++;
                            // 如果达到目标次数，将任务取消，否则更新时间
                            if (time != null && count.intValue() == time.intValue()) {
                                // 取消任务
                                currentScheduledFuture.cancel(true);
                                TIME_MAP.remove(currentScheduledFuture);
                            } else {
                                // 更新次数
                                TIME_MAP.put(currentScheduledFuture, count);
                            }
                        },
                        delayedTime,
                        periodTime,
                        timeUnit);
        scheduledFutures[0] = scheduledFuture;
        TIME_MAP.put(scheduledFuture, 0);
    }
}
