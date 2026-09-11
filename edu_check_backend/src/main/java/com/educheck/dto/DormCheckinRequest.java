package com.educheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

// 自动生成 getter/setter 方法
@Data
// Swagger 文档注解：描述这个实体类的作用
@Schema(description = "查寝打卡请求")
public class DormCheckinRequest {

    // Swagger 注解：对字段做文档描述
    @Schema(description = "纬度")
    private BigDecimal locationLat;

    @Schema(description = "经度")
    private BigDecimal locationLng;

    @Schema(description = "定位地址")
    private String locationAddr;

    @Schema(description = "宿舍楼")
    private String building;

    @Schema(description = "宿舍号")
    private String room;

    @Schema(description = "是否在宿舍区域")
    private Boolean inDormArea;

    @Schema(description = "人脸图片base64（打卡验证用）")
    private String faceImage;

    @Schema(description = "是否已通过人脸验证")
    private Boolean faceVerified;
}