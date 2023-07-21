package com.wecgcm.bilibili.exception;

/**
 * @author ：wecgwm
 * @date ：2023/07/11 16:37
 */
public class MinioException extends RuntimeException{

    public MinioException(String msg) {
        super(msg);
    }

    public MinioException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public MinioException(Throwable cause) {
        super(cause);
    }
}
