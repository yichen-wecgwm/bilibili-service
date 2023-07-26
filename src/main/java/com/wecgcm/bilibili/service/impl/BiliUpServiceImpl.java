package com.wecgcm.bilibili.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.CharPool;
import cn.hutool.core.util.ReUtil;
import com.wecgcm.bilibili.exception.ProcessException;
import com.wecgcm.bilibili.model.arg.BiliUpArg;
import com.wecgcm.bilibili.model.arg.MinioArg;
import com.wecgcm.bilibili.model.resp.BaiduTransResp;
import com.wecgcm.bilibili.service.BiliUpService;
import com.wecgcm.bilibili.service.TranslateService;
import com.wecgcm.bilibili.util.LogUtil;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author ：wecgwm
 * @date ：2023/07/21 0:29
 */
@Slf4j
@RequiredArgsConstructor(onConstructor_= @Autowired)
@Service
public class BiliUpServiceImpl implements BiliUpService {
    private final BiliUpArg biliUpArg;
    private final TranslateService translateService;
    private static final String DELIMITER = "";
    private static final Pattern BLANK_PATTERN = Pattern.compile(" +");
    // 中英文空格和常见标点符号
    private static final String TITLE_REGEX = "[\u4E00-\u9FFF\\w\\s!?.,]+";
    private static final Pattern TITLE_PATTERN = Pattern.compile(TITLE_REGEX);

    @Value("${bili-up.title-prefix}")
    private String titlePrefix;

    @Override
    public String upload(String videoId, String videoTitle) {
        videoTitle = processTitle(videoTitle);
        // todo retry
        List<String> args = biliUpArg.build(videoId, videoTitle);
        Timer.Sample timer = Timer.start();

        ProcessBuilder processBuilder = new ProcessBuilder(args)
                .redirectOutput(ProcessBuilder.Redirect.DISCARD);
        Process process = Try.of(processBuilder::start)
                .getOrElseThrow(ProcessException::new);
        Try.success(process)
                .andThenTry(Process::waitFor)
                .filter(p -> p.exitValue() == 0, p -> {
                    LogUtil.error(process.errorReader(), this.getClass());
                    return new ProcessException("process exit error");
                })
                .andFinally(process::destroy)
                .getOrElseThrow(ProcessException::new);

        timer.stop(Timer.builder("yt-dlp-dl")
                .register(Metrics.globalRegistry));
        log.info("upload done, videoId: {}, title: {}", videoId, videoTitle);
        //noinspection ResultOfMethodCallIgnored
        new File(MinioArg.Video.fileName(videoId)).delete();
        return videoId;
    }

    private String processTitle(String videoTitle) {
        if (!Validator.isMatchRegex(TITLE_PATTERN, videoTitle)) {
            BaiduTransResp baiduTransResp = translateService.baiduTrans(videoTitle);
            // 删除非法字符
            videoTitle = String.join(DELIMITER, ReUtil.findAllGroup0(TITLE_PATTERN, baiduTransResp.getTransResult().get(0).getDst()));
        }
        // 删除下划线
        videoTitle = videoTitle.chars().mapToObj(__ -> (char) __).filter(c -> c != CharPool.UNDERLINE).collect(StringBuffer::new, StringBuffer::append, StringBuffer::append).toString();
        // 删除多余空格
        videoTitle = BLANK_PATTERN.matcher(videoTitle).replaceAll(String.valueOf(CharPool.SPACE));
        return CharPool.DOUBLE_QUOTES + titlePrefix + videoTitle + CharPool.DOUBLE_QUOTES;
    }

}
