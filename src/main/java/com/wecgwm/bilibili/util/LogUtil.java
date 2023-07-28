package com.wecgwm.bilibili.util;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;

/**
 * @author ：wecgwm
 * @date ：2023/07/18 23:03
 */
@Slf4j
public class LogUtil {

    public static void error(BufferedReader reader, Class<?> clazz){
        reader.lines().forEach(s -> LoggerFactory.getLogger(clazz).error(s));
        Try.success(reader).andThenTry(BufferedReader::close).get();
    }

    public static void recordOnExceptionHandler(Thread t, Throwable e) {
        log.error("thread:{}, msg:{}", t, e.getMessage(), e);
        Counter.builder("exception.handler")
                .tag("exception", e.getClass().getSimpleName())
                .tag("thread", t.getName())
                .register(Metrics.globalRegistry)
                .increment();
    }


}
