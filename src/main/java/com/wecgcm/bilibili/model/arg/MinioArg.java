package com.wecgcm.bilibili.model.arg;

/**
 * @author ：wecgwm
 * @date ：2023/07/25 19:09
 */
public class MinioArg {
    public static final String SLASH = "/";
    private static final String VIDEO_EXT = ".webm";
    private static final String THUMBNAIL_EXT = ".webp";
    private static final String OUT_PUT_DIR = "videos" + SLASH;
    private static final String VIDEO_BUCKET_NAME = "videos";
    private static final String TITLE = "title";
    private static final String ARCHIVE = "archive";
    private static final String LOCK = "lock";

    public static class Video {
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

    public static class Thumbnail {
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

    public static class Title {
        public static String object(String videoId) {
            return videoId + MinioArg.SLASH + TITLE;
        }

        public static String bucket() {
            return VIDEO_BUCKET_NAME;
        }
    }

    public static class Archive {
        public static String object(String videoId) {
            return ARCHIVE + MinioArg.SLASH + videoId;
        }

        public static String bucket() {
            return VIDEO_BUCKET_NAME;
        }
    }

    public static class Lock {
        public static String object(String videoId) {
            return videoId + MinioArg.SLASH + LOCK;
        }

        public static String bucket() {
            return VIDEO_BUCKET_NAME;
        }
    }
}
