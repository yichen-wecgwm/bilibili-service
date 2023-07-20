package com.wecgcm.bilibili.exception.handler;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ：wecgwm
 * @date ：2023/07/20 23:15
 */
@Slf4j
public class BaseExceptionHandler {

    public static final String EMPTY = "";

    public static void recordOnExceptionHandler(Thread t, Throwable e){
        System.out.println("123");
        log.error("thread:{}, msg:{}", t == null ? EMPTY : t, e.getMessage(), e);
        Counter.Builder exception = Counter.builder("exception.handler")
                .tag("exception", e.getClass().getSimpleName());
        if (t != null) {
            exception.tag("thread", t.getName());
        }
        exception.register(Metrics.globalRegistry)
                .increment();
    }

}