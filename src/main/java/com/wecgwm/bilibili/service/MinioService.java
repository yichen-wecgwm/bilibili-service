package com.wecgwm.bilibili.service;


import io.minio.ObjectWriteResponse;

import java.io.InputStream;

public interface MinioService {

    InputStream get(String bucket, String object);

    void download(String bucket, String object, String fileName, boolean override);

    ObjectWriteResponse put(String bucket, String object, String text);

    void remove(String bucket, String object);
}
