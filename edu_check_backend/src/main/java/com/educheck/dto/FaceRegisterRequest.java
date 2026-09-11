package com.educheck.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "接受前端人脸注册数据")


public class FaceRegisterRequest {
    @Schema(description = "接受前端人脸照片")
    private String image;
    @Schema(description = "图片格式，如png、jpg，默认jpg")
    private String format="jpg";
}
