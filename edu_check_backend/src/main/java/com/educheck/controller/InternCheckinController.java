package com.educheck.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.educheck.common.Result;
import com.educheck.common.TokenContextHolder;
import com.educheck.dto.InternCheckinRequest;
import com.educheck.dto.PhotoUploadRequest;
import com.educheck.entity.CheckinStats;
import com.educheck.entity.InternCheckin;
import com.educheck.entity.InternPhoto;
import com.educheck.entity.Internship;
import com.educheck.service.CheckinStatsService;
import com.educheck.service.InternCheckinService;
import com.educheck.service.InternPhotoService;
import com.educheck.service.InternshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/intern")
//给final修饰的变量创建对象
@RequiredArgsConstructor
//用于文档生成
@Tag( name ="实习打卡",description = "实习打卡接口")
public class InternCheckinController {
    // 创建对应service
    private final InternshipService internshipService;
    private final InternCheckinService internCheckinService;
    private final TokenContextHolder tokenContextHolder;
    private final InternPhotoService internPhotoService;
    private final CheckinStatsService checkinStatsService;
//    private final CheckinStatsService checkinStatsService;

    @GetMapping("/stats")
//    川文档提示
    @Operation(summary = "获取实习统计")
    public Result<Map<String, Object>> stats() {
//        11 获取用户id
        Long userId = tokenContextHolder.requireCurrentUserId();

        Internship internship = internshipService.lambdaQuery()
//        11 匹配当前登录用户I0
                .eq(Internship::getUserId, userId)
//筛选实习状态:进行中active
                .eq(Internship::getStatus, "active")
///查询单条记录?
                .one();
// 定义Map存储要返回给前端的统计数据
        Map<String, Object> data = new HashMap<>();
//        1判断用户存在有效实习记录时，组装统计数据
        if (internship != null) {
// 统计该用户所有实习打卡记录总条数= 已完成打卡天数
            long completedDays = internCheckinService.lambdaQuery()
                    .eq(InternCheckin::getUserId, userId)
                    .count();
//            1计算实习进度百分比:总实习天数大于0才计算，否则进度为0
            int progress = internship.getTotalDays()
                    != null && internship.getTotalDays() > 0
                    ? (int) (completedDays * 100 /
                    internship.getTotalDays()) : 0;
//存入实习公司名称
            data.put("company", internship.getCompany());
//            1存入实习岗位
            data.put("role", internship.getRole());
///1 存入已打卡天数
            data.put("completedDays", completedDays);
/// 存入实习要求总天数
            data.put("totalDays", internship.getTotalDays());
//存入进度，限制最大值100，防止打卡超出总天数后进度超过100%
//存入进度，限制最大值100，防止打卡超出总天数后进度超过100%
            data.put("progress", Math.min(progress, 100));
        }
//                    / 封装统一成功响应返回前端，data为空时前端拿到空Map
        return Result.success(data);
    }

    @GetMapping("/history")
// 用于生成文档
    @Operation(summary = "获取打卡记录(分页)")
    public Result<Page<Map<String, Object>>> history(
//                    /接收前端分页页码参数，不传默认值为1
            @RequestParam(defaultValue = "1") int page,
// 接收前端每页条数参数，不传默认值为20
            @RequestParam(defaultValue = "20") int size) {
//获取用户id
        Long userId = tokenContextHolder.requireCurrentUserId();
// MyBatis-PLus分页查询:查询当前用户实习打卡记录，按日期倒序(最新记录在前)
        Page<InternCheckin> checkinPage = internCheckinService.page(
                new Page<>(page, size),
                new LambdaQueryWrapper<InternCheckin>()
                        .eq(InternCheckin::getUserId, userId)
                        .orderByDesc(InternCheckin::getDate));

// 构建包含图片的分页结果
// 遍历分页查询出来的原始打卡实体列表，组装成前端需要的Map结构，并关联查询图片
        List<Map<String, Object>> records =

                checkinPage.getRecords().stream().map(c -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", c.getId());
                    item.put("day", c.getDayNumber());
                    item.put("date", c.getDate());

                    item.put("time", c.getCheckinTime().toLocalTime().toString());
                    item.put("time", c.getCheckinTime().toLocalTime().toString());
                    item.put("log", c.getLogContent());
                    item.put("status", c.getStatus());


                    List<InternPhoto> photos = internPhotoService.lambdaQuery()
// 关联条件:图片归属当前这条打卡记录
                            .eq(InternPhoto::getCheckinId, c.getId())
// 按图片排序号升序，保证前端展示顺序统
                            .orderByAsc(InternPhoto::getSortOrder)
                            .list();
                    item.put("photos", photos.stream()
                            .map(InternPhoto::getUrl).collect(Collectors.toList()));
                    return item;
// 将stream流转ist集合
                }).collect(Collectors.toList());
        // 包装为分页对象，前端可通过.records 获取列表
//                1 创建泛型为Map的全新分页对象，复用数据库查询得到的分页参数
        Page<Map<String, Object>> pageResult = new Page<>(checkinPage.getCurrent(),
                checkinPage.getSize(), checkinPage.getTotal());
//                1/ 把组装好、带图片的打卡列表放入分页对象
        pageResult.setRecords(records);
//                1 封装统一成功返回体，分页数据返回前端
        return Result.success(pageResult);
    }

    @GetMapping("/my")
//                用于生成文档
    @Operation(summary = "获取我的实习信息")
    public Result<Internship> myInternship() {
//1获取用户id
        Long userId = tokenContextHolder.requireCurrentUserId();
//MyBatis-PLus链式条件查询，查询单条实习记录
        Internship internship = internshipService.lambdaQuery()
                .eq(Internship::getUserId, userId)
//筛选实习状态为active(进行中、有效实习)
                .eq(Internship::getStatus, "active")
                .one();
//封装统一成功响应，把实习实体返回给前端
        return Result.success(internship);
    }



    @PostMapping("/checkin")
    @Operation(summary = "实习打卡")
    public Result<Map<String, Object>> checkin(
            @RequestBody InternCheckinRequest request) {
        Long userId = tokenContextHolder.requireCurrentUserId();
        // 人脸验证检查
        if (request.getFaceVerified() == null ||
                !request.getFaceVerified()) {
            return Result.error("请先完成人脸验证");
        }
        // 检查今日是否已打卡
        InternCheckin existing = internCheckinService.lambdaQuery()
                // 用当前用户筛选
                .eq(InternCheckin::getUserId, userId)
                // 用今天日期筛选（只包括年月日）
                .eq(InternCheckin::getDate, LocalDate.now())
                .one();
        if (existing != null) {
            return Result.error("今日已打卡");
        }
        // 计算第N天打卡（等于之前打卡次数+1）
        Long dayNumber = internCheckinService.lambdaQuery()
                .eq(InternCheckin::getUserId, userId)
                .count() + 1;
        // 保存今天实习打卡记录
        InternCheckin checkin = new InternCheckin();
        checkin.setUserId(userId);
        // 设置实习id
        checkin.setInternshipId(request.getInternshipId());
        checkin.setDate(LocalDate.now());
        checkin.setCheckinTime(LocalDateTime.now());
        // 设置第n天打卡
        checkin.setDayNumber(Integer.valueOf(dayNumber.toString()));
        // 设置经纬度
        checkin.setLocationLat(request.getLocationLat());
        checkin.setLocationLng(request.getLocationLng());
        // 设置定位地址
        checkin.setLocationAddr(request.getLocationAddr());
        // 设置在公司区域内
        checkin.setInCompanyArea(1);
        // 设置工作日志
        checkin.setLogContent(request.getLogContent());
        // 设置完成状态
        checkin.setStatus("completed");
        // 保存实习打卡记录
        internCheckinService.save(checkin);
        // 更新实习进度
        // 如果实习表 id不为空
        if (request.getInternshipId() != null) {
            // 根据实习表id查到实习记录
            Internship internship =
                    internshipService.getById(request.getInternshipId());
            // 如果实习记录不为空
            if (internship != null) {
                // 给实习记录已完成天数设置内容
                // 之前如果为空 设置为 1天
                // 之前有内容 实习天数+1
                internship.setCompletedDays(
                        internship.getCompletedDays() != null
                                ? internship.getCompletedDays() + 1 : 1);
                // 更新实习记录表
                internshipService.updateById(internship);
            }
        }
        // 更新打卡统计表
        // 查到用户的打卡统计记录
        CheckinStats stats = checkinStatsService.lambdaQuery()
                .eq(CheckinStats::getUserId, userId).one();
        // 如果打卡统计记录不为空
        if (stats != null) {
            // 打卡统计记录里面 实习打卡记录+1
            stats.setInternTotal(stats.getInternTotal() + 1);
            // 更新打卡统计表
            checkinStatsService.updateById(stats);
        } else {
            // 打卡统计表记录为空
            // 创建该用户的打卡统计记录
            CheckinStats newStats = new CheckinStats();
            newStats.setUserId(userId);
            newStats.setDormTotal(0);
            newStats.setClassTotal(0);
            newStats.setInternTotal(1);
            newStats.setStreakDays(0);
            newStats.setTotalPoints(0);
            // 插入该用户的打卡统计记录
            checkinStatsService.save(newStats);
        }
        // 打包返回值给前端
        Map<String, Object> data = new HashMap<>();
        data.put("id", checkin.getId());
        data.put("dayNumber", dayNumber);
        data.put("status", "completed");
        return Result.success("打卡成功", data);
    }

    @PostMapping("/photo/upload")
    @Operation(summary = "上传打卡图片")
    public Result<InternPhoto> uploadPhoto(@RequestBody PhotoUploadRequest request) {
        // 创建实体图片实体
        InternPhoto photo = new InternPhoto();
        // 设置实习打卡id
        photo.setCheckinId(request.getCheckinId());
        // 设置图片地址
        photo.setUrl(request.getImagUrl());
        // 获取该实习最后一张实习图片
        InternPhoto maxPhoto = internPhotoService.lambdaQuery()
                // 用实习id过滤
                .eq(InternPhoto::getCheckinId, request.getCheckinId())
                // 根据里面排序字段倒序排列
                .orderByDesc(InternPhoto::getSortOrder)
                // 取一张
                .last("LIMIT 1")
                .one();
        // 设置实习图片 排序字段 值
        // 如果 该实习没有存储过图片 存0
        // 该实习之前有存储图片 最近一张图片 排序字段 上+1
        photo.setSortOrder(maxPhoto != null ? maxPhoto.getSortOrder() + 1 : 0);
        // 存储实习图片记录
        internPhotoService.save(photo);
        return Result.success("上传成功", photo);
    }
}
