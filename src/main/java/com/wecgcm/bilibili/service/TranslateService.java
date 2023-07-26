package com.wecgcm.bilibili.service;

import com.wecgcm.bilibili.model.resp.BaiduTransResp;

public interface TranslateService {

    BaiduTransResp baiduTrans(String src);

}
