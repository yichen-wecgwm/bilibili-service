package com.wecgwm.bilibili.service;

import java.util.concurrent.CompletionStage;

public interface BilibiliVideoService {
    CompletionStage<Void> upload(String videoId);

    /**
     * @param videoId videoId e.g. JpTqSzm4JOk in www.youtube.com/watch?v=JpTqSzm4JOk
     * @return videoInfoJson
     */
    String downloadVideoAndInfo(String videoId);

    void clean(String videoId);
}
