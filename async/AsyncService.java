package async;

import async.task.AsyncTask;
import async.task.AsyncTaskChain;
import async.task.AsyncVoidTask;

/**
 * @author hzq
 * @version 1.0
 * @description 异步服务
 * @date 2024/5/8 16:36
 */
@SuppressWarnings("unchecked")
public interface AsyncService {

    /**
     * 等待所有任务执行结束后,将执行结果封装到对应的AsyncTask中,默认会阻塞主线程
     * @param isBlock 是否阻塞主线程
     * @param tasks 任务列表
     * @param <T> 任务结果类型
     */
    <T> void run(boolean isBlock,AsyncTask<T>... tasks);
    <T> void run(AsyncTask<T>... tasks);

    /**
     * 等待上一个线程执行完成后依次执行后面的任务,执行结果封装到对应的AsyncTaskChain中,默认会阻塞主线程
     * @param isBlock 是否阻塞主线程
     * @param tasks 任务列表
     * @param <T> 上一个线程的执行结果
     * @param <R> 当前线程的返回结果
     */
    <T,R> void run(boolean isBlock,AsyncTaskChain<T,R>... tasks);
    <T,R> void run(AsyncTaskChain<T,R>... tasks);

    /**
     * 异步执行,无返回结果
     * @param isBlock 是否阻塞主线程
     * @param tasks 任务列表
     */
    void run(boolean isBlock,AsyncVoidTask... tasks);
    void run(AsyncVoidTask... tasks);
}
