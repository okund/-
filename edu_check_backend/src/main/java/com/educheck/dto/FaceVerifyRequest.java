package com.educheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

// 自动生成 getter/setter 方法
@Data
// Swagger文档注解：描述这个实体类的作用
@Schema(description = "人脸验证请求")
public class FaceVerifyRequest {

    // Swagger注解：对字段做文档描述
    @Schema(description = "当前采集的人脸图片 base64 数据")
    private String image;

    // Swagger注解：对字段做文档描述
    @Schema(description = "图片格式，如 png、jpg，默认 jpg")
    private String format = "jpg";
}