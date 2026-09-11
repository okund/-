package com.educheck.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

// lombok自动生成get/set/toString等基础方法
@Data
// 绑定当前实体映射数据库中的checkin_stats表
@TableName("checkin_stats")
public class CheckinStats {

    // 标记该字段为主键，主键自增策略
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 查寝打卡总数 */
    private Integer dormTotal;

    /** 上课签到总数 */
    private Integer classTotal;

    /** 实习打卡总数 */
    private Integer internTotal;

    /** 连续打卡天数 */
    private Integer streakDays;

    /** 总积分 */
    private Integer totalPoints;

    // 开启自动填充：插入、更新数据时都会自动刷新为当前时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}