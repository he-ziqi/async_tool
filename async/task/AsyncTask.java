package async.task;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author hzq
 * @version 1.0
 * @description 异步任务,带返回值
 * @date 2024/5/11 15:31
 */
@AllArgsConstructor
@Data
@SuppressWarnings("all")
public class AsyncTask<R> implements CallableTask{

    //任务名称
    private final String taskName;

    //发生异常时的默认值 默认为Null
    private R exDefaultValue;

    //任务执行结果
    private R result;

    //任务提供接口
    private final Supplier<R> task;

    //异常处理handler
    private Function<? super Throwable,? super R> exHandler;

    //获取任务结果的超时时间
    private long timeout = DEFAULT_TIMEOUT;

    //获取任务结果的超时时间单位
    private TimeUnit timeUnit = DEFAULT_TIMEUNIT;

    //任务是否执行结束
    private volatile boolean completed = false;

    public AsyncTask(String taskName, Supplier<R> task) {
        this.taskName = taskName;
        this.task = task;
    }

    public AsyncTask(String taskName, Supplier<R> task,TimeUnit timeUnit,long timeout) {
        this.taskName = taskName;
        this.task = task;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    public AsyncTask(String taskName, Supplier<R> task, Function<? super Throwable, ? super R> exHandler) {
        this.taskName = taskName;
        this.task = task;
        this.exHandler = exHandler;
    }

    public AsyncTask(String taskName, Supplier<R> task, Function<? super Throwable, ? super R> exHandler,TimeUnit timeUnit,long timeout) {
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
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    @Override
    public <T> void setResult(T result) {
        this.result = (R) result;
    }

    @Override
    public boolean isComplete() {
        return completed;
    }
}
