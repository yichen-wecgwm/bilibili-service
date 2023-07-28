package com.wecgwm.bilibili.model.req;

import lombok.Builder;
import lombok.Data;

/**
 * @author ：wecgwm
 * @date ：2023/07/26 13:55
 */
@Builder
@Data
public class BaiduTransReq {

    private String q;
    private String from;
    private String to;
    private String appid;
    private String salt;
    private String sign;

}
