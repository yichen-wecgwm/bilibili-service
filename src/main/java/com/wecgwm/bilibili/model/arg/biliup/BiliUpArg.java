package com.wecgwm.bilibili.model.arg.biliup;

import cn.hutool.core.text.CharPool;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.ImmutableList;
import com.wecgwm.bilibili.model.arg.minio.MinioThumbnailArg;
import com.wecgwm.bilibili.model.arg.minio.MinioVideoArg;
import com.wecgwm.bilibili.model.dto.VideoInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ：wecgwm
 * @date ：2023/07/18 23:28
 */
@Slf4j
@Component
public class BiliUpArg {
    private static final String UPLOAD_COMMAND = "upload";
    private static final String SOURCE_OP = "--source";
    private static final String COPYRIGHT_OP = "--copyright";
    private static final String COPYRIGHT_REPRODUCED = "2";
    private static final String LINE_OP = "--line";
    private static final String CONCURRENT_OP = "--limit";
    private static final String CATEGORY_OP = "--tid";
    private static final String CATEGORY_ID_VARIETY = "71";
    private static final String TAG_OP = "--tag";
    private static final String TITLE_OP = "--title";
    private static final String COVER_OP = "--cover";
    @Value("${bili-up.path}")
    private String biliUpPath;
    @Value("${bili-up.line}")
    public String LINE;
    @Value("${bili-up.concurrent.thread}")
    public String CONCURRENT_THREAD_CNT;
    @Value("${bili-up.tag}")
    public String TAG;

    public List<String> build(String videoId, VideoInfoDto videoInfo) {
        String tag = videoInfo.getTag() == null ? TAG : Stream.of(TAG, Strings.join(videoInfo.getTag().tagList(), CharPool.COMMA)).filter(StrUtil::isNotBlank).collect(Collectors.joining(StrPool.COMMA));
        List<String> ret = ImmutableList.<String>builder()
                .add(biliUpPath)
                .add(UPLOAD_COMMAND)
                .add(SOURCE_OP)
                .add(videoId.replaceAll("-", ""))
                .add(COPYRIGHT_OP)
                .add(COPYRIGHT_REPRODUCED)
                .add(CATEGORY_OP)
                .add(CATEGORY_ID_VARIETY)
                .add(TAG_OP)
                .add(tag)
                .add(LINE_OP)
                .add(LINE)
                .add(CONCURRENT_OP)
                .add(CONCURRENT_THREAD_CNT)
                .add(TITLE_OP)
                .add(videoInfo.getTitle())
                .add(COVER_OP)
                .add(MinioThumbnailArg.fileName(videoId))
                .add(MinioVideoArg.fileName(videoId))
                .build();
        log.info(String.join(" ", ret));
        return ret;
    }
}
