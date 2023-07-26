package com.wecgcm.bilibili.model.resp;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author ：wecgwm
 * @date ：2023/07/26 13:55
 */
@Builder
@Data
public class BaiduTransResp {
    private String from;
    private String to;
    private String errorCode;
    private List<TransResult> transResult;

    @Data
    public static class TransResult{
        private String src;
        private String dst;
    }

}
