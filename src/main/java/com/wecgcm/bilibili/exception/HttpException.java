package com.wecgcm.bilibili.exception;

/**
 * @author ：wecgwm
 * @date ：2023/07/11 16:37
 */
public class HttpException extends RuntimeException{

    public HttpException(String msg) {
        super(msg);
    }

    public HttpException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public HttpException(Throwable cause) {
        super(cause);
    }
}
