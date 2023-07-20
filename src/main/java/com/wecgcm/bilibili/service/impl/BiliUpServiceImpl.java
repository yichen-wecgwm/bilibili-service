package com.wecgcm.bilibili.service.impl;

import com.wecgcm.bilibili.exception.ProcessException;
import com.wecgcm.bilibili.model.arg.BiliUpArg;
import com.wecgcm.bilibili.service.BiliUpService;
import com.wecgcm.bilibili.util.LogUtil;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ：wecgwm
 * @date ：2023/07/21 0:29
 */
@Slf4j
@RequiredArgsConstructor(onConstructor_= @Autowired)
@Service
public class BiliUpServiceImpl implements BiliUpService {
    private final BiliUpArg biliUpArg;

    @Override
    public void upload(String videoId, String videoTitle) {
        List<String> args = biliUpArg.build(videoId, videoTitle);
        Timer.Sample timer = Timer.start();

        ProcessBuilder processBuilder = new ProcessBuilder(args)
                .redirectOutput(ProcessBuilder.Redirect.DISCARD);
        Process process = Try.of(processBuilder::start)
                .getOrElseThrow(ProcessException::new);
        Try.success(process)
                .andThenTry(Process::waitFor)
                .filter(p -> p.exitValue() == 0, p -> {
                    LogUtil.error(process.getErrorStream(), this.getClass());
                    return new ProcessException("process exit error");
                })
                .andFinally(process::destroy)
                .getOrElseThrow(ProcessException::new);

        timer.stop(Timer.builder("yt-dlp-dl")
                .register(Metrics.globalRegistry));
        log.info("upload done, videoId: {}, title: {}", videoId, videoTitle);
    }

}
