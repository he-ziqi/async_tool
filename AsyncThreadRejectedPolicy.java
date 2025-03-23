package com.example.demo.juc.async.config;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author hzq
 * @version 1.0
 * @description 线程池拒绝策略
 * @date 2024/6/18 12:45
 */
@Slf4j
public class AsyncThreadRejectedPolicy implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        try {
            executor.getQueue().put(r);
        } catch (InterruptedException e) {
            log.error("线程任务已满,重新放入任务队列失败,拒绝执行,task:{},executor:{}", r, executor);
            Thread.currentThread().interrupt();
        }
    }
}
