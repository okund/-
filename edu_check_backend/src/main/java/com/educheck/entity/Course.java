package com.educheck.entity;
/**
 * zhang26/9/11 v1
 */

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("course")
public class Course {
    @TableId(type = IdType.AUTO)
    private  Long id;
    private String name;
    private String teacher;
    private String location;
    private String startTime;
    private String endTime;
    private String section;
    private String weekDay;
    private Integer weekStart;
    private Integer weekEnd;
    private Long teacherId;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
