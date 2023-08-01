package com.wecgwm.bilibili;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wecgwm.bilibili.service.BiliUpService;
import com.wecgwm.bilibili.service.BilibiliVideoService;
import com.wecgwm.bilibili.service.TranslateService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutionException;

/**
 * @author ：wecgwm
 * @date ：2023/07/21 15:51
 */
@Slf4j
@RequiredArgsConstructor(onConstructor_= @Autowired)
@SpringBootTest(classes = BilibiliServiceApplication.class)
public class BilibiliTest {
    private final BilibiliVideoService bilibiliVideoService;
    private final BiliUpService biliUpService;
    private final TranslateService translateService;
    public static ObjectMapper INSTANCE = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    @Test
    public void sampleTest() throws JsonProcessingException {
        String jsonStr2 = JSONUtil.toJsonStr("{    \"url\": \"https://www.youtube.com/@15ya.fullmoon/videos\",    \"titlePrefix\": \"[十五夜] \"}");
        ChannelDto list3 = INSTANCE.readValue(jsonStr2, new TypeReference<>() {
        });
        list3 = this.<ChannelDto>temp(jsonStr2);
    }

    private <T> T temp(String json) throws JsonProcessingException {
        return INSTANCE.readValue(json, new TypeReference<>() {
        });
    }

    @Test
    public void readJsonTest() throws JsonProcessingException, ExecutionException, InterruptedException {
        bilibiliVideoService.upload("EW0IPfC_il8").toCompletableFuture().get();
    }

    @Test
    public void baiduTranslateTest(){
        log.info(translateService.baiduTrans("apple").toString());
    }

    @Data
    public static class ChannelDto {
        private String url;
    }

    @Test
    public void titleTest(){
        biliUpService.upload("123", "abc 应该被翻译 - _ - asf");
        biliUpService.upload("123", "abc 不应该被翻译  _");
        biliUpService.upload("123", "[아이유의 팔레트\uD83C\uDFA8] 뉴진스의 컬러 스위치요 (With 뉴진스) Ep.21");
    }

    @Test
    public void uploadTest(){
        biliUpService.upload("FvD8-DH04FY", "123 5");
    }

}
