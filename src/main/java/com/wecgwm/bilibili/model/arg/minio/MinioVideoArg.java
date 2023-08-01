package com.wecgwm.bilibili.model.arg.minio;

/**
 * @author ：wecgwm
 * @date ：2023/07/31 23:42
 */
public final class MinioVideoArg extends MinioArg {

    public static String object(String videoId) {
        return videoId + SLASH + videoId + VIDEO_EXT;
    }

    public static String bucket() {
        return VIDEO_BUCKET_NAME;
    }

    public static String fileName(String videoId){
        return MinioArg.OUT_PUT_DIR + videoId + VIDEO_EXT;
    }

}
