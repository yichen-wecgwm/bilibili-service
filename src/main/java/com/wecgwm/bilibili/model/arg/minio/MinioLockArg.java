package com.wecgwm.bilibili.model.arg.minio;

/**
 * @author ：wecgwm
 * @date ：2023/07/31 23:46
 */
public final class MinioLockArg extends MinioArg {

    public static String object(String videoId) {
        return videoId + MinioArg.SLASH + LOCK;
    }

    public static String bucket() {
        return VIDEO_BUCKET_NAME;
    }

}
