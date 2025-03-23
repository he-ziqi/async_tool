package async.task;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author hzq
 * @version 1.0
 * @description 异步任务,按顺序执行,带上一个线程的返回值,有返回结果
 * @date 2024/5/17 14:52
 */
@AllArgsConstructor
@Data
@SuppressWarnings("all")
public class AsyncTaskChain<T,R> implements CallableTask{

    //任务名称
    private final String taskName;

    //发生异常时的默认值 默认为Null
    private R exDefaultValue = null;

    //任务执行结果
    private R result;

    //任务提供接口
    private final Function<T,Supplier<R>> task;

    //异常处理handler
    private Function<? super Throwable,? super R> exHandler;

    //获取任务结果的超时时间
    private long timeout = DEFAULT_TIMEOUT;

    //获取任务结果的超时时间单位
    private TimeUnit timeUnit = DEFAULT_TIMEUNIT;

    //任务是否执行结束
    private volatile boolean completed = false;

    public AsyncTaskChain(String taskName, Function<T,Supplier<R>> task) {
        this.taskName = taskName;
        this.task = task;
    }

    public AsyncTaskChain(String taskName, Function<T,Supplier<R>> task,long timeout,TimeUnit timeUnit) {
        this.taskName = taskName;
        this.task = task;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    public AsyncTaskChain(String taskName, Function<T,Supplier<R>> task, Function<? super Throwable, ? super R> exHandler) {
        this.taskName = taskName;
        this.task = task;
        this.exHandler = exHandler;
    }

    public AsyncTaskChain(String taskName, Function<T,Supplier<R>> task, Function<? super Throwable, ? super R> exHandler,long timeout,TimeUnit timeUnit) {
        this.taskName = taskName;
        this.task = task;
        this.exHandler = exHandler;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public <T> void setResult(T result) {
        this.result = (R) result;
    }

    @Override
    public boolean isComplete() {
        return this.completed;
    }
}
