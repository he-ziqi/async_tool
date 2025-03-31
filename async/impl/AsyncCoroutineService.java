package async.impl;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberExecutorScheduler;
import async.AsyncHandler;
import async.function.SwitchHandler;
import async.task.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author hzq
 * @version 1.0
 * @description 异步协程服务
 * @date 2024/6/6 20:03
 */
@Service
@Slf4j
@SuppressWarnings("all")
public class AsyncCoroutineService implements AsyncHandler, InitializingBean {

    @Resource(name = "asyncExecutor")
    private ExecutorService asyncExecutor;

    private FiberExecutorScheduler scheduler = new FiberExecutorScheduler("fiberScheduler", Executors.newFixedThreadPool(100));

    @Override
    public <T> void run(AsyncTask<T>... tasks) {
        Task[] taskParent = tasksCheck(tasks);
        tasks = new AsyncTask[taskParent.length];
        for (int i = 0; i < taskParent.length; i++) {
            tasks[i] = (AsyncTask<T>) taskParent[i];
        }
        Fiber<? super T>[] fibers = generator(tasks);
        for (int i = 0; i < fibers.length; i++) {
            try {
                tasks[i].setResult(tasks[i].getTimeout() == CallableTask.DEFAULT_TIMEOUT ? fibers[i].get() : fibers[i].get(tasks[i].getTimeout(),tasks[i].getTimeUnit()));
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                log.error("获取协程处理结果时异常,当前执行任务[{}],异常信息:{}", tasks[i].getTaskName(), e);
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
                () -> CompletableFuture.runAsync(() -> {
                    run(tasks);
                },asyncExecutor)
    }

    @Override
    public <T, R> void run(AsyncTaskChain<T, R>... tasks) {
        Task[] taskParent = tasksCheck(tasks);
        tasks = new AsyncTaskChain[taskParent.length];
        for (int i = 0; i < taskParent.length; i++) {
            tasks[i] = (AsyncTaskChain<T,R>) taskParent[i];
        }
        Fiber<? super R>[] fibers = generator(tasks);
        for (int i = 0; i < fibers.length; i++) {
            try {
                tasks[i].setResult(tasks[i].getTimeout() == CallableTask.DEFAULT_TIMEOUT ? fibers[i].get() : fibers[i].get(tasks[i].getTimeout(), tasks[i].getTimeUnit()));
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                log.error("获取协程处理结果时异常,当前执行任务[{}],异常信息:{}", tasks[i].getTaskName(), e);
                resultExHandler(tasks[i], e);
            } finally {
                tasks[i].setCompleted(true);
            }
        }
    }

    @Override
    public <T, R> void run(boolean isBlock, AsyncTaskChain<T, R>... tasks) {
        SwitchHandler.handler(isBlock).execute(
                () -> run(tasks),
                () -> scheduler.newFiber(() -> {
                    run(tasks);
                    return null;
                }).start()
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
            scheduler.newFiber(() -> {
                try {
                    task.getTask().run();
                } catch (Throwable e) {
                    log.error("协程任务运行异常,当前执行任务[{}],异常信息:{}", task.getTaskName(), e);
                    exHandler(Objects.isNull(task.getExHandler()) ? null : ex -> {
                        task.getExHandler().accept(e);
                        return null;
                    }, e, null, null, task.getTaskName());
                } finally {
                    task.setCompleted(true);
                }
                return null;
            }).start();
        }
    }

    @Override
    public void run(boolean isBlock, AsyncVoidTask... tasks) {
        SwitchHandler.handler(isBlock).execute(
                () -> run(tasks),
                () -> scheduler.newFiber(() -> {
                    run(tasks);
                    return null;
                }).start()
        );
    }

    private <T> Fiber<T>[] generator(AsyncTask<T>... tasks) {
        List<Fiber<T>> fiberList = new ArrayList<>(tasks.length);
        for (int i = 0; i < tasks.length; i++) {
            AsyncTask<T> task = tasks[i];
            fiberList.add((Fiber<T>) scheduler.newFiber(() -> {
                T result = null;
                try {
                    result = task.getTask().get();
                } catch (Throwable e) {
                    log.error("协程任务运行异常,当前执行任务[{}],异常信息:{}", task.getTaskName(), e);
                    return exHandler(task.getExHandler(), e, result, task.getExDefaultValue(), task.getTaskName());
                }
                return result;
            }));
            fiberList.get(i).start();
        }
        return fiberList.toArray(new Fiber[fiberList.size()]);
    }

    private <T, R> Fiber<? super R>[] generator(AsyncTaskChain<T, R>... tasks) {
        List<Fiber<? super R>> fiberList = new ArrayList<>(tasks.length);
        R preResult = null;
        for (int i = 0; i < tasks.length; i++) {
            AsyncTaskChain<T, R> task = tasks[i];
            try {
                if(i >> 1 != 0){
                    preResult = (R) fiberList.get(i - 1).get();
                }
                R finalPreResult = preResult;
                fiberList.add(scheduler.newFiber(() -> {
                    R result = null;
                    try {
                        result = task.getTask().apply((T) finalPreResult).get();
                    } catch (Throwable e) {
                        log.error("协程任务运行异常,当前执行任务[{}],异常原因:{},异常信息:", task.getTaskName(), e.getMessage(),e);
                        return exHandler(task.getExHandler(), e, result, task.getExDefaultValue(), task.getTaskName());
                    }
                    return result;
                }));
                fiberList.get(i).start();
            } catch (ExecutionException | InterruptedException e) {
                log.error("获取协程处理结果时异常,当前执行任务[{}],异常原因:{},异常信息:", task.getTaskName(), e.getMessage(),e);
            }
        }
        return fiberList.toArray(new Fiber[fiberList.size()]);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.scheduler = new FiberExecutorScheduler("fiberScheduler", this.asyncExecutor);
    }
}
