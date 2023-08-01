package com.wecgwm.bilibili.model.arg.minio;

/**
 * @author ：wecgwm
 * @date ：2023/07/31 23:45
 */
public final class MinioThumbnailArg extends MinioArg {

    public static String object(String videoId) {
        return videoId + SLASH + videoId + THUMBNAIL_EXT;
    }

    public static String bucket() {
        return VIDEO_BUCKET_NAME;
    }

    public static String fileName(String videoId){
        return MinioArg.OUT_PUT_DIR + videoId + THUMBNAIL_EXT;
    }

}
