package com.example.demo.juc.async.task;

import java.util.concurrent.TimeUnit;

/**
 * @author hzq
 * @version 1.0
 * @description 有返回值的任务
 * @date 2024/6/14 13:05
 */
public interface CallableTask extends Task{

    TimeUnit DEFAULT_TIMEUNIT = TimeUnit.SECONDS;

    long DEFAULT_TIMEOUT = 0L;

    default long getTimeout(){
        return DEFAULT_TIMEOUT;
    }

    default TimeUnit getTimeUnit(){
        return DEFAULT_TIMEUNIT;
    }

    <T> void setResult(T result);

    <R> R getExDefaultValue();
}
