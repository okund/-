package com.educheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "实习打卡图片上传请求")
public class PhotoUploadRequest {
    @Schema(description = "打卡记录ID")
    private Long checkinId;
    @Schema(description = "图片URL或临时路径")
    private String imagUrl;
}
