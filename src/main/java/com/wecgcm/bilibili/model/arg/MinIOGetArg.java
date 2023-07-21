package com.wecgcm.bilibili.model.arg;

import io.minio.GetObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author ：wecgwm
 * @date ：2023/07/18 23:28
 */
@Slf4j
@Component
public class MinIOGetArg {
    private static final String BUCKET_NAME = "videos";
    private static final String SLASH = "/";
    public static final String TITLE = "title";

    public GetObjectArgs build(String videoId) {
        return GetObjectArgs
                .builder()
                .bucket(BUCKET_NAME)
                .object(videoId + SLASH + MinIOGetArg.TITLE)
                .build();
    }

}
