package com.wecgcm.bilibili.model.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCode {
    DEFAULT_SUCCESS("200", "success"),
    DEFAULT_ERROR("500", "unknown_exception");

    private final String code;
    private final String msg;
}
