package com.wecgcm.bilibili.controller;

import com.wecgcm.bilibili.model.req.BilibiliUploadRequest;
import com.wecgcm.bilibili.model.resp.Response;
import com.wecgcm.bilibili.service.BilibiliVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：wecgwm
 * @date ：2023/07/19 1:04
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("${spring.application.name}/video")
public class BilibiliVideoController {
    private final BilibiliVideoService bilibiliVideoService;

    @PostMapping("/upload")
    public Response<String> download(@RequestBody BilibiliUploadRequest request){
        bilibiliVideoService.upload(request.getVideoId());
        return Response.ok();
    }

}
