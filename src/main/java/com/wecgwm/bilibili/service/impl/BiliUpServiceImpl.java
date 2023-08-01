package com.wecgwm.bilibili.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.CharPool;
import cn.hutool.core.util.ReUtil;
import com.wecgwm.bilibili.config.ObjectMapperSingleton;
import com.wecgwm.bilibili.exception.ProcessException;
import com.wecgwm.bilibili.model.arg.biliup.BiliUpArg;
import com.wecgwm.bilibili.model.dto.Tag;
import com.wecgwm.bilibili.model.dto.VideoInfoDto;
import com.wecgwm.bilibili.model.resp.BaiduTransResp;
import com.wecgwm.bilibili.service.BiliUpService;
import com.wecgwm.bilibili.service.TranslateService;
import com.wecgwm.bilibili.util.LogUtil;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
    private static final String DEV = "dev";
    private static final String DELIMITER = "";
    private static final Pattern BLANK_PATTERN = Pattern.compile(" +");
    // 中英文空格和常见标点符号
    private static final String TITLE_REGEX = "[\u4E00-\u9FFF\\w\\s!?.,]+";
    private static final Pattern TITLE_PATTERN = Pattern.compile(TITLE_REGEX);

    @Value("${spring.profiles.active}")
    private String env;
    @Value("${bili-up.title-prefix}")
    private String titlePrefix;

    @SneakyThrows
    @Override
    public String upload(String videoId, String videoInfoJson) {
        VideoInfoDto videoInfo = ObjectMapperSingleton.INSTANCE.readValue(videoInfoJson, VideoInfoDto.class);
        videoInfo.setTitle(processTitle(videoInfo.getTitle()));
        if (!CollectionUtils.isEmpty(videoInfo.getExt())) {
            videoInfo.setTag(ObjectMapperSingleton.INSTANCE.readValue(videoInfo.getExt().get(0), Tag.class));
        }
        // todo retry
        List<String> args = biliUpArg.build(videoId, videoInfo);
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
        log.info("upload done, videoId: {}, title: {}", videoId, videoInfo);
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
        // 删除多余空格, 添加前缀
        videoTitle = titlePrefix + BLANK_PATTERN.matcher(videoTitle).replaceAll(String.valueOf(CharPool.SPACE));
        if (DEV.equals(env)) {
            videoTitle = CharPool.DOUBLE_QUOTES + videoTitle + CharPool.DOUBLE_QUOTES;
        }
        return videoTitle;
    }

}
