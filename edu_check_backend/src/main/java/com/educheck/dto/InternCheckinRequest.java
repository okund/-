package com.educheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "实习打卡请求")
public class InternCheckinRequest {
    @Schema(description = "实习ID")
    private Long internshipId;

    @Schema(description = "纬度")
    private BigDecimal locationLat;

    @Schema(description = "经度")
    private BigDecimal locationLng;

    @Schema(description = "定位地址")
    private String locationAddr;

    @Schema(description = "工作日志")
    private String logContent;

    @Schema(description = "是否已通过人脸验证")
    private Boolean faceVerified;

}
