package com.educheck.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.educheck.common.Result;
import com.educheck.common.TokenContextHolder;
import com.educheck.entity.DormCheckin;
import com.educheck.service.DormCheckinService;
import com.educheck.service.CheckinStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dorm")
@RequiredArgsConstructor
@Tag(name = "查寝打卡", description = "查寝打卡接口")
public class DormCheckinController {

    private final DormCheckinService dormCheckinService;
    private final TokenContextHolder tokenContextHolder;

    private final CheckinStatsService checkinStatsService;
    @GetMapping("/history")
    @Operation(summary = "获取打卡记录")
    public Result<Page<DormCheckin>> history(
            // 接收URL传参page，无传参时默认值为1（第一页）
            @RequestParam(defaultValue = "1") int page,
            // 接收URL传参size，无传参时默认每页20条数据
            @RequestParam(defaultValue = "20") int size) {

        // 获取用户id
        Long userId = tokenContextHolder.requireCurrentUserId();

        LambdaQueryWrapper<DormCheckin> wrapper =
                new LambdaQueryWrapper<DormCheckin>()
                        // 用户id相等 查询
                        .eq(DormCheckin::getUserId, userId)
                        // 排序：先按打卡日期倒序，日期相同按打卡时间倒序（最新记录在前）
                        // getDate 精确到日
                        .orderByDesc(DormCheckin::getDate,
                                DormCheckin::getCheckinTime);

        // 将查询结果分页返回
        return Result.success(
                dormCheckinService.page(
                        new Page<>(page, size), wrapper));
    }

}