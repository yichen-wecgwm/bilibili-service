package com.wecgcm.bilibili.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ：wecgwm
 * @date ：2023/07/16 18:30
 */
@Configuration
public class MinioClientConfig {
    @Value("${minio.endpoint}")
    private String endPoint;
    @Value("${minio.credentials.access-key}")
    private String accessKey;
    @Value("${minio.credentials.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient(){
        return MinioClient.builder()
                        .endpoint(endPoint)
                        .credentials(accessKey, secretKey)
                        .build();
    }

}
