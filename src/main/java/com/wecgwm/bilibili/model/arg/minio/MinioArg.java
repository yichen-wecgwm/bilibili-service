package com.wecgwm.bilibili.model.arg.minio;

/**
 * @author ：wecgwm
 * @date ：2023/07/25 19:09
 */
public sealed abstract class MinioArg permits MinioArchiveArg, MinioLockArg, MinioThumbnailArg, MinioVideoInfoArg, MinioVideoArg{
    public static final String SLASH = "/";
    static final String VIDEO_EXT = ".webm";
    static final String THUMBNAIL_EXT = ".png";
    static final String OUT_PUT_DIR = "videos" + SLASH;
    static final String VIDEO_BUCKET_NAME = "videos";
    static final String VIDEO_INFO = "video-info";
    static final String ARCHIVE = "archive";
    static final String LOCK = "lock";
}
