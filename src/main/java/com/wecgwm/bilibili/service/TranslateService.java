package com.wecgwm.bilibili.service;

import com.wecgwm.bilibili.model.resp.BaiduTransResp;

public interface TranslateService {

    BaiduTransResp baiduTrans(String src);

}
