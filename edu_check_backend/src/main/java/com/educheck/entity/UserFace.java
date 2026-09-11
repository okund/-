package com.educheck.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

/**
 *
 */

@Data
@TableName("user_face")
public class UserFace {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String faceUrl;
    private String faceToken;
    private Integer registered;
    private Integer version;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}