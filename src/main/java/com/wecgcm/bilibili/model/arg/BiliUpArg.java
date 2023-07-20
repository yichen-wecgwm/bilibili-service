package com.wecgcm.bilibili.model.arg;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author ：wecgwm
 * @date ：2023/07/18 23:28
 */
@Slf4j
@Component
public class BiliUpArg {
    private static final String UPLOAD_COMMAND = "upload";
    private static final String SOURCE_OP = "--source";
    private static final String YOUTUBE_VIDEO_URL_PREFIX = "https://www.youtube.com/watch?v=";
    private static final String COPYRIGHT_OP = "--copyright";
    private static final String COPYRIGHT_REPRODUCED = "2";
    private static final String LINE_OP = "--line";
    private static final String LINE_BAIDU = "bda2";
    private static final String CONCURRENT_OP = "--limit";
    private static final String CATEGORY_OP = "--tid";
    private static final String CATEGORY_ID_VARIETY = "71";
    private static final String TAG_OP = "--tag";
    private static final String TITLE_OP = "--title";
    @Value("${bili-up.path}")
    private String biliUpPath;
    @Value("${bili-up.concurrent.thread}")
    public String CONCURRENT_THREAD_CNT;
    @Value("${bili-up.tag}")
    public String TAG;

    public List<String> build(String videoId, String title) {
        List<String> ret = ImmutableList.<String>builder()
                .add(biliUpPath)
                .add(UPLOAD_COMMAND)
                .add(SOURCE_OP)
                .add(YOUTUBE_VIDEO_URL_PREFIX + videoId)
                .add(COPYRIGHT_OP)
                .add(COPYRIGHT_REPRODUCED)
                .add(CATEGORY_OP)
                .add(CATEGORY_ID_VARIETY)
                .add(TAG_OP)
                .add(TAG)
                .add(LINE_OP)
                .add(LINE_BAIDU)
                .add(CONCURRENT_OP)
                .add(CONCURRENT_THREAD_CNT)
                .add(TITLE_OP)
                .add(title)
                .add(MinIODownloadArg.OUT_PUT_DIR + videoId + MinIODownloadArg.VIDEO_EXT)
                .build();
        log.info(String.join(" ", ret));
        return ret;
    }
}
