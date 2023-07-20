package com.wecgcm.bilibili.service;


public interface MinioService {

    /**
     * @param videoId videoId e.g. JpTqSzm4JOk in www.youtube.com/watch?v=JpTqSzm4JOk
     * @return videoTitle
     */
    String downloadVideo(String videoId);
}
