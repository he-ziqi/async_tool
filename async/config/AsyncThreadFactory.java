package com.example.demo.juc.async.config;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hzq
 * @version 1.0
 * @description 异步线程工厂
 * @date 2024/6/18 12:39
 */
public class AsyncThreadFactory implements ThreadFactory {

    private final String namePrefix;
    private final boolean daemon;
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    public AsyncThreadFactory(String namePrefix, boolean daemon) {
        this.namePrefix = namePrefix;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(namePrefix + "-thread-" + threadNumber.getAndIncrement());
        thread.setDaemon(daemon);
        return thread;
    }
}
