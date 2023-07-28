package com.wecgwm.bilibili.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONUtil;
import com.wecgwm.bilibili.exception.HttpException;
import com.wecgwm.bilibili.model.req.BaiduTransReq;
import com.wecgwm.bilibili.model.resp.BaiduTransResp;
import com.wecgwm.bilibili.service.TranslateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

/**
 * @author ：wecgwm
 * @date ：2023/07/26 13:43
 */
@Slf4j
@RequiredArgsConstructor(onConstructor_= @Autowired)
@Service
public class TranslateServiceImpl implements TranslateService {
    public static final String FROM_AUTO = "auto";
    public static final String CHINESE = "zh";
    private final OkHttpClient okHttpClient;
    @Value("${baidu.trans.app-id}")
    private String baiduAppId;
    @Value("${baidu.trans.key}")
    private String baiduKey;
    private static final String BAIDU_TRANS_API = "https://fanyi-api.baidu.com/api/trans/vip/translate";

    @Override
    public BaiduTransResp baiduTrans(String src) {
        String salt = UUID.fastUUID().toString(true);
        String sign = baiduAppId + src + salt + baiduKey;
        BaiduTransReq param = BaiduTransReq.builder()
                .q(src)
                .from(FROM_AUTO)
                .to(CHINESE)
                .appid(baiduAppId)
                .salt(salt)
                .sign(MD5.create().digestHex(sign))
                .build();
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BAIDU_TRANS_API)).newBuilder();
        JSONUtil.parseObj(param).forEach(entry -> urlBuilder.addQueryParameter(entry.getKey(), entry.getValue().toString()));
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()){
                throw new HttpException("baidu translate Unexpected code: " + response);
            }
            BaiduTransResp ret = JSONUtil.toBean(Objects.requireNonNull(response.body()).string(), BaiduTransResp.class);
            if (ret.getErrorCode() != null) {
                log.error("baidu translate error, error code:{}", ret.getErrorCode());
                throw new HttpException("baidu translate error code not null: " + ret);
            }
            return ret;
        } catch (IOException e) {
            throw new HttpException("baidu translate http exception", e);
        }
    }

}
