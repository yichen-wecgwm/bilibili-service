package com.wecgcm.bilibili.config;

import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author ：wecgwm
 * @date ：2023/07/24 23:05
 */
@Configuration
public class OkHttpClientConfig {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Bean
    public OkHttpClient okHttpClient(){
        return new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(30, 5, TimeUnit.SECONDS))
                .build();
    }

}
