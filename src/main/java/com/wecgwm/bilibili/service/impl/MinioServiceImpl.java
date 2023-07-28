package com.wecgwm.bilibili.service.impl;

import com.wecgwm.bilibili.exception.MinioException;
import com.wecgwm.bilibili.service.MinioService;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.minio.*;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * @author ：wecgwm
 * @date ：2023/07/16 19:32
 */
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class MinioServiceImpl implements MinioService {
    private final MinioClient minioClient;

    @Override
    public InputStream get(String bucket, String object) {
        return Try.of(() -> GetObjectArgs
                        .builder()
                        .bucket(bucket)
                        .object(object)
                        .build()
                )
                .mapTry(minioClient::getObject)
                .getOrElseThrow(MinioException::new);
    }

    @Override
    public void download(String bucket, String object, String fileName, boolean override) {
        Timer.Sample timer = Timer.start();

        DownloadObjectArgs args = DownloadObjectArgs
                .builder()
                .bucket(bucket)
                .object(object)
                .filename(fileName)
                .overwrite(override)
                .build();

        Try.success(args)
                .andThenTry(minioClient::downloadObject)
                .getOrElseThrow(MinioException::new);

        timer.stop(Timer.builder("minio-download").register(Metrics.globalRegistry));

        log.info("download done, bucket:{}, object: {}, fileName:{}", bucket, object, fileName);
    }

    @Override
    public ObjectWriteResponse put(String bucket, String object, String text) {
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream = Try.success(pipedOutputStream)
                .mapTry(PipedInputStream::new)
                .getOrElseThrow(MinioException::new);
        Try.of(() -> new BufferedWriter(new OutputStreamWriter(pipedOutputStream)))
                .andThenTry(bufferedWriter -> bufferedWriter.write(text))
                .andThenTry(BufferedWriter::flush)
                .andThenTry(BufferedWriter::close)
                .getOrElseThrow(MinioException::new);
        return Try.of(() -> PutObjectArgs
                        .builder()
                        .bucket(bucket)
                        .object(object)
                        .stream(pipedInputStream, -1, 10485760)
                        .build())
                .mapTry(minioClient::putObject)
                .andThenTry(resp -> log.info("put done, thread:{}, bucket: {}, object:{}, text:{}, eTag:{}, resp:{}", Thread.currentThread(), bucket, object, text, resp.etag(), resp.object()))
                .getOrElseThrow(MinioException::new);
    }

    @Override
    public void remove(String bucket, String object) {
        Try.run(() -> minioClient
                        .removeObject(RemoveObjectArgs
                                .builder()
                                .bucket(bucket)
                                .object(object)
                                .build()))
                .getOrElseThrow(MinioException::new);
    }
}