package com.wecgwm.bilibili.service;

public interface BilibiliVideoService {
    void upload(String videoId);

    /**
     * @param videoId videoId e.g. JpTqSzm4JOk in www.youtube.com/watch?v=JpTqSzm4JOk
     * @return videoTitle
     */
    String downloadAndGetTitle(String videoId);

    void clean(String videoId);
}
