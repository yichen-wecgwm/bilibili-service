package com.wecgcm.bilibili.service.impl;

import com.wecgcm.bilibili.exception.MinioException;
import com.wecgcm.bilibili.model.arg.MinIODownloadArg;
import com.wecgcm.bilibili.model.arg.MinIOGetArg;
import com.wecgcm.bilibili.service.MinioService;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.minio.MinioClient;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author ：wecgwm
 * @date ：2023/07/16 19:32
 */
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class MinioServiceImpl implements MinioService {
    private final MinioClient minioClient;
    private final MinIODownloadArg minIODownloadArg;
    private final MinIOGetArg minIOGetArg;

    @Override
    public String downloadVideo(String videoId) {
        Timer.Sample timer = Timer.start();

        Try.success(videoId)
                .map(minIODownloadArg::build)
                .andThenTry(minioClient::downloadObject)
                .recoverWith(e -> Try.failure(new MinioException("minio download: download exception", e)))
                .getOrElseThrow(MinioException::new);

        timer.stop(Timer.builder("minio-download").register(Metrics.globalRegistry));

        log.info("download done, videoId: {}", videoId);

        InputStream resp = Try.success(videoId)
                .map(minIOGetArg::build)
                .mapTry(minioClient::getObject)
                .getOrElseThrow(MinioException::new);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resp));
        return Try.success(bufferedReader)
                .mapTry(BufferedReader::readLine)
                .andFinallyTry(bufferedReader::close)
                .get();
    }
}