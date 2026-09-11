package com.educheck.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("dorm_checkin")
public class DormCheckin {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学生用户ID */
    private Long userId;

    /** 打卡日期 */
    private LocalDate date;

    /** 打卡时间 */
    private LocalDateTime checkinTime;

    /** 定位纬度 */
    private BigDecimal locationLat;

    /** 定位经度 */
    private BigDecimal locationLng;

    /** 定位地址 */
    private String locationAddr;

    /** 宿舍楼名称 */
    private String building;

    /** 宿舍号 */
    private String room;

    /** 是否在宿舍区域: 0否 1是 */
    private Integer inDormArea;

    /** 人脸验证是否通过: 0否 1是 */
    private Integer faceVerified;

    /** 人脸图片URL */
    private String faceImageUrl;

    /** 状态: normal/late */
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}