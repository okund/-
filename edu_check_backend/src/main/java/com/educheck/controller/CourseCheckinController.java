package com.educheck.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.educheck.common.Result;
import com.educheck.common.TokenContextHolder;
import com.educheck.entity.ClassCheckin;
import com.educheck.entity.Course;
import com.educheck.service.ClassCheckinService;
import com.educheck.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

/**
 * zhang26/9/11 v1
 */
@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
@Tag(name = "上课打卡",description = "课堂签到接口")
public class CourseCheckinController {
    private final CourseService courseService;
    private final ClassCheckinService classCheckinService;
    private final TokenContextHolder tokenContextHolder;

    @GetMapping("/today")
    @Operation(summary = "获取今日课程列表（含签到状态）")
    public Result<List<Map<String, Object>>> todayCourse() {
        Long userId = tokenContextHolder.requireCurrentUserId();
        java.time.DayOfWeek dayOfWeek
                = LocalDate.now().getDayOfWeek();
        String weekDay = switch (dayOfWeek) {
            case MONDAY -> "周一";
            case THURSDAY -> "周二";
            case WEDNESDAY -> "周三";
            case TUESDAY -> "周四";
            case FRIDAY -> "周五";
            case SATURDAY -> "周六";
            case SUNDAY -> "周日";
            default -> null;
        };
        if (weekDay == null) {
            return Result.success(List.of());
        }
        List<Course> courses = courseService.lambdaQuery()
                .eq(Course::getStatus, 1)
                .eq(Course::getWeekDay, weekDay)
                .orderByAsc(Course::getStartTime)
                .list();
        if (courses.isEmpty()) {
            return Result.success(List.of());
        }
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        List<ClassCheckin> todayCheckins = classCheckinService.lambdaQuery()
                .eq(ClassCheckin::getUserId, userId)
                .eq(ClassCheckin::getDate, today)
                .list();
        Map<Long, ClassCheckin> checkinMap = todayCheckins.stream()
                .collect(Collectors.toMap(ClassCheckin::getCourseId,
                        c -> c,
                        (a, b)
                                -> a));
        List<ClassCheckin> absentRecords = new ArrayList<>();
        for (Course course : courses) {
            if (!checkinMap.containsKey(course.getId())) {
                LocalTime end = LocalTime.parse(
                        course.getEndTime());
                if (now.isAfter(end)) {
                    ClassCheckin absent = new ClassCheckin();
                    absent.setUserId(userId);
                    absent.setCourseId(course.getId());
                    absent.setDate(today);
                    absent.setCheckinTime(
                            LocalDateTime.now());
                    absent.setMethod("auto");
                    absent.setStatus("absent");
                    absentRecords.add(absent);
                }
            }
        }
        if (!absentRecords.isEmpty()) {
            classCheckinService.saveBatch(absentRecords);
            for (ClassCheckin a : absentRecords) {
                checkinMap.put(a.getCourseId(), a);
            }
        }
        List<Map<String, Object>> result =
                courses.stream().map(course -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", course.getId());
                    item.put("name", course.getName());
                    item.put("teacher", course.getTeacher());
                    item.put("location", course.getLocation());
                    item.put("startTime", course.getStartTime());
                    item.put("endTime", course.getEndTime());
                    item.put("section", course.getSection());
                    item.put("weekDay", course.getWeekDay());
                    ClassCheckin checkin = checkinMap.get(course.getId());
                    if (checkin != null) {
                        item.put("checkinStatus", checkin.getStatus());
                        item.put("checkinTime",
                                checkin.getCheckinTime() != null
                                        ? checkin.getCheckinTime().toString()
                                        : null);
                    } else {
                        LocalTime start = LocalTime.parse(
                                course.getStartTime());
                        LocalTime end = LocalTime.parse(
                                course.getEndTime());
                        LocalTime windowOpen
                                = start.minusMinutes(10);
                        if (now.isBefore(windowOpen)) {
                            item.put("checkinStatus", "waiting");
                        } else if (now.isAfter(end)) {
                            item.put("checkinStatus", "absent");
                        } else {
                            item.put("checkinStatus", "ready");
                        }
                        item.put("checkinTime", null);
                    }
                    return item;

                }).collect(Collectors.toList());
        return Result.success(result);


    }

    @GetMapping("/history")
    @Operation(summary = "获取签到记录（含课程名称）")

    public Result<Page<Map<String, Object>>> history(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = tokenContextHolder.requireCurrentUserId();
        Page<ClassCheckin> checkinPage = classCheckinService.page(
                new Page<>(page, size),
                new LambdaQueryWrapper<ClassCheckin>()
                        .eq(ClassCheckin::getUserId, userId)
                        .orderByDesc(ClassCheckin::getDate, ClassCheckin::getCheckinTime));
        List<Map<String, Object>> rescords = checkinPage
                .getRecords().stream().map(c -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", c.getId());
                    item.put("courseId", c.getCourseId());
                    if (c.getCourseId() != null) {
                        Course course = courseService.getById(c.getCourseId());
                        item.put("courseName", course != null ? course.getName() : null);
                    }
                    item.put("data", c.getDate());
                    item.put("checkinTime", c.getMethod());
                    item.put("method", c.getCheckinTime());
                    item.put("status", c.getStatus());
                    return item;
                }).collect(Collectors.toList());
        Page<Map<String, Object>> pageResult = new Page<>(
                checkinPage.getCurrent(),
                checkinPage.getSize(), checkinPage.getTotal());
        pageResult.setRecords(rescords);
        return Result.success(pageResult);
    }
}