package com.wecgcm.bilibili.service.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wecgcm.bilibili.exception.handler.BaseExceptionHandler;
import com.wecgcm.bilibili.service.BiliUpService;
import com.wecgcm.bilibili.service.BilibiliVideoService;
import com.wecgcm.bilibili.service.MinioService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Metrics;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ：wecgwm
 * @date ：2023/07/19 1:06
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class BilibiliVideoServiceImpl implements BilibiliVideoService {
    private final MinioService minioService;
    private final BiliUpService biliUpService;

    private final ThreadPoolExecutor DOWNLOAD_AND_UPLOAD_THREAD_POOL = new ThreadPoolExecutor(2, 10, 60, TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(5), new ThreadFactoryBuilder().setNameFormat("bilibili-video-%d").build());

    {
        Gauge.builder("thread.pool.active.bilibili.video", DOWNLOAD_AND_UPLOAD_THREAD_POOL::getActiveCount)
                .tag("active", "count")
                .register(Metrics.globalRegistry);
        Gauge.builder("thread.pool.queue.bilibili.video", () -> DOWNLOAD_AND_UPLOAD_THREAD_POOL.getQueue().size())
                .tag("queue", "size")
                .register(Metrics.globalRegistry);
    }

    @Override
    public void upload(String videoId) {
        CompletableFuture.completedStage(videoId)
                .thenApplyAsync(minioService::downloadVideo, DOWNLOAD_AND_UPLOAD_THREAD_POOL)
                .thenAccept(title -> biliUpService.upload(videoId, title))
                .exceptionally(e -> {
                    BaseExceptionHandler.recordOnExceptionHandler(Thread.currentThread(), e);
                    return new CompletableFuture<Void>().resultNow();
                });

    }

}