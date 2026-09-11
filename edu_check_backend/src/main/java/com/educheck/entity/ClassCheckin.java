package com.educheck.entity;
/**
 * zhang26/9/11 v1
 */

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("class_checkin")

public class ClassCheckin {
    @TableId(type = IdType.AUTO)
    private  Long id;
    private Long userId;
    private Long courseId;
    private LocalDate date;
    private LocalDateTime checkinTime;
    private String method;
    private BigDecimal locationLat;
    private BigDecimal locationLng;
    private String locationAddr;
    private String dynamicCode;
    private String status;
    private String sessionId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

}
