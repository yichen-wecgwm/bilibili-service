package com.wecgcm.bilibili.model.resp;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Optional;

/**
 * @author ：wecgwm
 * @date ：2023/07/10 20:45
 */
@Builder
@Data
@ToString
public class Response<T> {
    private String code;
    private String msg;
    private T data;

    public static <T> Response<T> ok() {
        return Response.<T>defaultSuccessBuilder().build();
    }

    public static <T> Response<T> from(T data) {
        return Response.<T>defaultSuccessBuilder().data(data).build();
    }

    public static Response<String> from(Throwable e, String code, String data) {
        return Response.<String>builder().code(code).msg(optionalCause(e)).data(data).build();
    }

    public static Response<String> from(Throwable e, String data) {
        return Response.<String>defaultErrorBuilder(e).data(data).build();
    }

    public static Response<String> from(Throwable e) {
        return Response.<String>defaultErrorBuilder(e).data(e.getMessage()).build();
    }

    private static <T> ResponseBuilder<T> defaultErrorBuilder(Throwable e){
        return Response.<T>builder().code(StatusCode.DEFAULT_ERROR.getCode()).msg(optionalCause(e));
    }

    private static <T> ResponseBuilder<T> defaultSuccessBuilder(){
        return Response.<T>builder().code(StatusCode.DEFAULT_SUCCESS.getCode()).msg(StatusCode.DEFAULT_SUCCESS.getMsg());
    }

    private static String optionalCause(Throwable e){
        return Optional.ofNullable(e.getCause()).orElse(e).getMessage();
    }
}
