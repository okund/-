package com.educheck.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("intern_checkin")
public class InternCheckin {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学生用户ID */
    private Long userId;

    /** 实习记录ID */
    private Long internshipId;
    private LocalDate date;
    /**打卡时间*/
    private LocalDateTime checkinTime;
    /**第N天打卡*/
    private Integer dayNumber;
    /**定位纬度*/
    private BigDecimal locationLat;
    /*定位经度*/
    private BigDecimal locationLng;
    /**定位地址*/
    private String locationAddr;
    /** 是否在公司区域:0否1是*/
    private Integer inCompanyArea;
    /** 工作口志内容*/
    private String logContent;
    /** 状态:completed */
    private String status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
