package com.wecgcm.bilibili.exception;

/**
 * @author ：wecgwm
 * @date ：2023/07/11 16:37
 */
public class ProcessException extends RuntimeException{

    public ProcessException(String msg) {
        super(msg);
    }

    public ProcessException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ProcessException(Throwable cause) {
        super(cause);
    }
}
