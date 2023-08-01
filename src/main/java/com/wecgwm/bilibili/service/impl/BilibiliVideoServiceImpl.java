package com.wecgwm.bilibili.service.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wecgwm.bilibili.model.arg.minio.*;
import com.wecgwm.bilibili.service.BiliUpService;
import com.wecgwm.bilibili.service.BilibiliVideoService;
import com.wecgwm.bilibili.service.MinioService;
import com.wecgwm.bilibili.util.LogUtil;
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
import java.util.concurrent.*;

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
    public CompletionStage<Void> upload(String videoId) {
        return CompletableFuture.completedStage(videoId)
                .thenApplyAsync(this::downloadVideoAndInfo, DOWNLOAD_AND_UPLOAD_THREAD_POOL)
                .thenApply(videoInfoJson -> biliUpService.upload(videoId, videoInfoJson))
                .thenAccept(this::clean)
                .exceptionally(e -> {
                    LogUtil.recordOnExceptionHandler(Thread.currentThread(), e);
                    return null;
                });
    }

    @Override
    public String downloadVideoAndInfo(String videoId) {
        minioService.download(MinioVideoArg.bucket(), MinioVideoArg.object(videoId), MinioVideoArg.fileName(videoId), true);
        minioService.download(MinioThumbnailArg.bucket(), MinioThumbnailArg.object(videoId), MinioThumbnailArg.fileName(videoId), true);
        InputStream resp = minioService.get(MinioVideoInfoArg.bucket(), MinioVideoInfoArg.object(videoId));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resp));
        return Try.success(bufferedReader)
                .mapTry(BufferedReader::readLine)
                .andFinallyTry(bufferedReader::close)
                .get();
    }

    @Override
    public void clean(String videoId) {
        minioService.put(MinioArchiveArg.bucket(), MinioArchiveArg.object(videoId), Thread.currentThread().getName());
        minioService.remove(MinioVideoArg.bucket(), MinioVideoArg.object(videoId));
        minioService.remove(MinioThumbnailArg.bucket(), MinioThumbnailArg.object(videoId));
        minioService.remove(MinioLockArg.bucket(), MinioLockArg.object(videoId));
        minioService.remove(MinioVideoInfoArg.bucket(), MinioVideoInfoArg.object(videoId));
        //noinspection ResultOfMethodCallIgnored
        new File(MinioVideoArg.fileName(videoId)).delete();
        //noinspection ResultOfMethodCallIgnored
        new File(MinioThumbnailArg.fileName(videoId)).delete();
    }

}