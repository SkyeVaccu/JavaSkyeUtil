package utils.websocket.rmi;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/** @Description 可以中途修改结果的FutureTask @Author Skye @Date 2022/11/28 17:00 */
public class ModifiableFutureTask<V> extends FutureTask<V> {
    // 既定的返回值
    private V result;

    /**
     * 生成一个FutureTask,由发起者决定返回值
     *
     * @param runnable 执行函数
     * @param result 返回值
     */
    public ModifiableFutureTask(Runnable runnable, V result) {
        super(runnable, result);
    }

    /**
     * 生成一个FutureTask,由发起者决定返回值
     *
     * @param callable 带返回值的对象
     */
    public ModifiableFutureTask(Callable<V> callable) {
        super(callable);
    }

    /** 生成一个空FutureTask对象，其用于在中途由其他对象来填充返回值 */
    public ModifiableFutureTask() {
        this(() -> {}, null);
    }

    /**
     * 在该任务还没有执行的时候，将该任务重新设置结果
     *
     * @param result 结果对象
     * @return 当前的Future对象
     */
    public ModifiableFutureTask<V> setResult(V result) {
        this.set(result);
        return this;
    }
}
