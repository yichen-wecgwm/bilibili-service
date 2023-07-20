package com.wecgcm.bilibili.exception.handler;

import com.wecgcm.bilibili.model.resp.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author ：wecgwm
 * @date ：2023/07/10 21:05
 */
@RestControllerAdvice
@Slf4j
public class BilibiliExceptionHandler extends BaseExceptionHandler{

    @ExceptionHandler(Exception.class)
    public Response<String> lastHandler(Throwable e){
        recordOnExceptionHandler(null, e);
        return Response.from(e);
    }

}