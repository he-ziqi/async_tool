package async.task;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Consumer;

/**
 * @author hzq
 * @version 1.0
 * @description 异步任务,无返回值
 * @date 2024/5/21 14:47
 */
@AllArgsConstructor
@Data
public class AsyncVoidTask implements Task{

    //任务名称
    private String taskName;

    //任务提供接口
    private final Runnable task;

    //异常处理handler
    private Consumer<? super Throwable> exHandler;

    //任务是否执行结束
    private volatile boolean completed = false;

    public AsyncVoidTask(String taskName, Runnable task) {
        this.taskName = taskName;
        this.task = task;
    }

    @Override
    public boolean isComplete() {
        return this.completed;
    }
}
