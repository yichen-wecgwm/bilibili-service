package com.wecgwm.bilibili.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ：wecgwm
 * @date ：2023/07/22 17:15
 */
public class ObjectMapperSingleton {

    public static ObjectMapper INSTANCE = new ObjectMapper()
            .findAndRegisterModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

}