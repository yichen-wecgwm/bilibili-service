package com.wecgcm.bilibili.service.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wecgcm.bilibili.model.arg.MinioArg;
import com.wecgcm.bilibili.service.BiliUpService;
import com.wecgcm.bilibili.service.BilibiliVideoService;
import com.wecgcm.bilibili.service.MinioService;
import com.wecgcm.bilibili.util.LogUtil;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Metrics;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
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
                .thenApplyAsync(this::downloadAndGetTitle, DOWNLOAD_AND_UPLOAD_THREAD_POOL)
                .thenApply(title -> biliUpService.upload(videoId, title))
                .thenAccept(this::clean)
                .exceptionally(e -> {
                    LogUtil.recordOnExceptionHandler(Thread.currentThread(), e);
                    return null;
                });

    }

    @Override
    public String downloadAndGetTitle(String videoId) {
        minioService.download(MinioArg.Video.bucket(), MinioArg.Video.object(videoId), MinioArg.Video.fileName(videoId), true);
        minioService.download(MinioArg.Thumbnail.bucket(), MinioArg.Thumbnail.object(videoId), MinioArg.Thumbnail.fileName(videoId), true);
        InputStream resp = minioService.get(MinioArg.Title.bucket(), MinioArg.Title.object(videoId));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resp));
        return Try.success(bufferedReader)
                .mapTry(BufferedReader::readLine)
                .andFinallyTry(bufferedReader::close)
                .get();
    }

    @Override
    public void clean(String videoId) {
        minioService.put(MinioArg.Archive.bucket(), MinioArg.Archive.object(videoId), Thread.currentThread().getName());
        minioService.remove(MinioArg.Video.bucket(), MinioArg.Video.object(videoId));
        minioService.remove(MinioArg.Lock.bucket(), MinioArg.Lock.object(videoId));
        minioService.remove(MinioArg.Title.bucket(), MinioArg.Title.object(videoId));
        //noinspection ResultOfMethodCallIgnored
        new File(MinioArg.Video.fileName(videoId)).delete();
        //noinspection ResultOfMethodCallIgnored
        new File(MinioArg.Thumbnail.fileName(videoId)).delete();
    }

}