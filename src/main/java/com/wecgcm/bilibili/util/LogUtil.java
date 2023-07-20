package com.wecgcm.bilibili.util;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author ：wecgwm
 * @date ：2023/07/18 23:03
 */
@Slf4j
public class LogUtil {
    public static final String EMPTY = "";

    public static void error(InputStream stream, Class<?> clazz){
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream))){
            reader.lines().forEach(s -> LoggerFactory.getLogger(clazz).error(s));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void recordOnExceptionHandler(Thread t, Throwable e){
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
