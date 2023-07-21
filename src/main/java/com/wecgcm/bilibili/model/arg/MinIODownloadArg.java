package com.wecgcm.bilibili.model.arg;

import io.minio.DownloadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author ：wecgwm
 * @date ：2023/07/18 23:28
 */
@Slf4j
@Component
public class MinIODownloadArg {
    private static final String BUCKET_NAME = "videos";
    private static final String SLASH = "/";
    public static final String VIDEO_EXT = ".webm";
    public static final String OUT_PUT_DIR = "videos" + SLASH;

    public DownloadObjectArgs build(String videoId) {
        return DownloadObjectArgs
                .builder()
                .bucket(BUCKET_NAME)
                .object(videoId + SLASH + videoId + MinIODownloadArg.VIDEO_EXT)
                .filename(filePath(videoId))
                .overwrite(true)
                .build();
    }

    public String filePath(String videoId){
        return OUT_PUT_DIR + videoId + MinIODownloadArg.VIDEO_EXT;
    }

}
