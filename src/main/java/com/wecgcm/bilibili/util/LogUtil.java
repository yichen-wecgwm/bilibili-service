package com.wecgcm.bilibili.util;

import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author ：wecgwm
 * @date ：2023/07/18 23:03
 */
public class LogUtil {

    public static void error(InputStream stream, Class<?> clazz){
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream))){
            reader.lines().forEach(s -> LoggerFactory.getLogger(clazz).error(s));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
