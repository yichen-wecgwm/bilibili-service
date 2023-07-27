package com.wecgwm.bilibili;

import com.wecgcm.bilibili.BilibiliServiceApplication;
import com.wecgcm.bilibili.service.BiliUpService;
import com.wecgcm.bilibili.service.TranslateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ：wecgwm
 * @date ：2023/07/21 15:51
 */
@Slf4j
@RequiredArgsConstructor(onConstructor_= @Autowired)
@SpringBootTest(classes = BilibiliServiceApplication.class)
public class BilibiliTest {
    private final BiliUpService biliUpService;
    private final TranslateService translateService;

    @Test
    public void baiduTranslateTest(){
        log.info(translateService.baiduTrans("apple").toString());
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
