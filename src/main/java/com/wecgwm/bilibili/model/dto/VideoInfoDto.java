package com.wecgwm.bilibili.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author ：wecgwm
 * @date ：2023/07/21 18:55
 */
@Data
public class VideoInfoDto {
    private String videoId;
    private String title;
    private LocalDate uploadDate;
    private List<String> ext;
    private Tag tag;
}
