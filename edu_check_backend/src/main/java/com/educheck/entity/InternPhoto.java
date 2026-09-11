package com.educheck.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("intern_photo")
public class InternPhoto {
    @TableId(type = IdType.AUTO)
    private Long id;
    /**实习打卡记录ID*/
    private Long checkinId;
    /** 图片URL*/
    private String url;
    /**排序序号*/
    private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
