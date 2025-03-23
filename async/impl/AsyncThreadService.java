package async.impl;

import async.AsyncHandler;
import async.function.SwitchHandler;
import async.task.AsyncTask;
import async.task.AsyncTaskChain;
import async.task.AsyncVoidTask;
import async.task.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

/**
 * @author hzq
 * @version 1.0
 * @description 异步线程服务
 * @date 2024/5/8 16:37
 */
@Service
@Slf4j
@SuppressWarnings("unchecked")
public class AsyncThreadService implements AsyncHandler {

    @Resource(name = "asyncExecutor")
    private ExecutorService asyncExecutor;

    @Override
    public <T> void run(AsyncTask<T>... tasks) {
        Task[] taskParent = tasksCheck(tasks);
        tasks = new AsyncTask[taskParent.length];
        for (int i = 0; i < taskParent.length; i++) {
            tasks[i] = (AsyncTask<T>) taskParent[i];
        }
        CompletableFuture<? super T>[] futures = generator(tasks);
        CompletableFuture.allOf(futures).join();
        for (int i = 0; i < futures.length; i++) {
            try {
                tasks[i].setResult((T) futures[i].get(tasks[i].getTimeout(),tasks[i].getTimeUnit()));
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error("获取线程处理结果时异常,当前执行任务[{}],异常信息:",tasks[i].getTaskName(),e);
                resultExHandler(tasks[i],e);
            } finally {
                tasks[i].setCompleted(true);
            }
        }
    }

    @Override
    public <T> void run(boolean isBlock, AsyncTask<T>... tasks) {
        SwitchHandler.handler(isBlock).execute(
                () -> run(tasks),
                () -> CompletableFuture.runAsync(() -> run(tasks),asyncExecutor)
        );
    }

    @Override
    public <T, R> void run(AsyncTaskChain<T, R>... tasks) {
        Task[] taskParent = tasksCheck(tasks);
        tasks = new AsyncTaskChain[taskParent.length];
        for (int i = 0; i < taskParent.length; i++) {
            tasks[i] = (AsyncTaskChain<T,R>) taskParent[i];
        }
        CompletableFuture<? super R>[] futures = generator(tasks);
        CompletableFuture.allOf(futures).join();
        for (int i = 0; i < futures.length; i++) {
            try {
                tasks[i].setResult((R) futures[i].get(tasks[i].getTimeout(),tasks[i].getTimeUnit()));
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error("获取线程处理结果时异常,当前执行任务[{}],异常信息:",tasks[i].getTaskName(),e);
                resultExHandler(tasks[i],e);
            } finally {
                tasks[i].setCompleted(true);
            }
        }
    }

    @Override
    public <T, R> void run(boolean isBlock, AsyncTaskChain<T, R>... tasks) {
        SwitchHandler.handler(isBlock).execute(
                () -> run(tasks),
                () -> CompletableFuture.runAsync(() -> run(tasks),asyncExecutor)
        );
    }

    @Override
    public void run(AsyncVoidTask... tasks) {
        Task[] taskParent = tasksCheck(tasks);
        tasks = new AsyncVoidTask[taskParent.length];
        for (int i = 0; i < taskParent.length; i++) {
            tasks[i] = (AsyncVoidTask) taskParent[i];
        }
        for (AsyncVoidTask task : tasks) {
            CompletableFuture.runAsync(task.getTask(),asyncExecutor)
                    .handle((r,e) -> exHandler(Objects.isNull(task.getExHandler()) ? null : ex -> {
                        task.getExHandler().accept(ex);
                        return null;
                    },e,null,null,task.getTaskName()));
        }
    }

    @Override
    public void run(boolean isBlock, AsyncVoidTask... tasks) {
        SwitchHandler.handler(isBlock).execute(
                () -> run(tasks),
                () -> CompletableFuture.runAsync(() -> run(tasks),asyncExecutor)
        );
    }

    private <T> CompletableFuture<? super T>[] generator(AsyncTask<T>... tasks){
        List<CompletableFuture<? super T>> futureList = new ArrayList<>(tasks.length);
        for (AsyncTask<T> task : tasks) {
            futureList.add(CompletableFuture.supplyAsync(task.getTask(), asyncExecutor)
                    .handle((r, e) -> exHandler(task.getExHandler(), e, r, task.getExDefaultValue(), task.getTaskName())));
        }
        return futureList.toArray(new CompletableFuture[0]);
    }

    private <T,R> CompletableFuture<? super R>[] generator(AsyncTaskChain<T,R>... tasks){
        CompletableFuture<? super R>[] futures = new CompletableFuture[tasks.length];
        futures[0] = CompletableFuture.supplyAsync(tasks[0].getTask().apply(null), asyncExecutor)
                .handle((r, e) -> exHandler(tasks[0].getExHandler(),e,r,tasks[0].getExDefaultValue(),tasks[0].getTaskName()));
        for (int i = 1; i < tasks.length; i++) {
            AsyncTaskChain<T,R> task = tasks[i];
            futures[i] = futures[i - 1].thenApplyAsync(r -> (task.getTask()).apply((T) r),asyncExecutor)
                    .handle((r, e) -> exHandler(task.getExHandler(), e, r, task.getExDefaultValue(), task.getTaskName()));
        }
        return futures;
    }
}
