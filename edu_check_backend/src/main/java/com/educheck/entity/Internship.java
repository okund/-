package com.educheck.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("internship")
public class Internship {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学生用户ID */
    private Long userId;

    /** 公司名称 */
    private String company;

    /** 实习岗位 */
    private String role;

    /** 实习开始日期 */
    private LocalDate startDate;

    /** 实习结束日期 */
    private LocalDate endDate;

    /** 总实习天数 */
    private Integer totalDays;

    /** 已完成天数 */
    private Integer completedDays;

    /** 状态: active/completed */
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
