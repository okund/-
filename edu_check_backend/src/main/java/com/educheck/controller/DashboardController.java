package com.educheck.controller;

import com.educheck.common.Result;
import com.educheck.common.TokenContextHolder;
import com.educheck.entity.User;
import com.educheck.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.SessionStatus;

import java.util.HashMap;
import java.util.Map;

// 标记为REST接口控制器，返回数据自动转为JSON格式
//表示当前Java是控制器
@RestController
// 定义当前控制器下所有接口的统一根路径
//配置前端请求这个控制器路径
@RequestMapping("/api/dashboard")
//生成文档时的注释·
//  用于创建service 对象
// 为所有final修饰的成员变量生成构造方法，替代@Autowired注入
@RequiredArgsConstructor
// 用于文档提示  方便对接,构造方法将
@Tag(name = "首页仪表盘", description = "首页数据总览接口")
public class DashboardController {

    // 绑定GET请求，接口完整地址：/api/dashboard/banners
    @GetMapping("/banners")
    // 文档提示
    @Operation(summary = "获取轮播图列表")
    // 接口业务方法，返回包装好的图片地址数组
    //    Result  公共类统一对应的返回对象  返回一个 字符串数组
    public Result<String[]> banners() {
        // 返回成功响应，内部封装三张轮播图静态资源路径数组
        //将多张图片封装成字符串数组，再进一步调用结果函数响应前端字符串
        return Result.success(new String[]{
                // 故意调转顺序 和默认数据区分
                "/images/banner_3.png",
                "/images/banner_2.png",
                "/images/banner_1.png"
        });
    }
    private final TokenContextHolder tokenContextHolder;
    //创建获得前端传过来用户编号工具类对象属性
    private final UserService userService;
    @RequestMapping("/overview")
    @Operation(summary = "获取前端首页的用户信息和打卡信息")
    public Result<Map<String,Object>>overview(){
        Long userid = tokenContextHolder.getCurrentUserId();
        //调用业务层根据用户编号获得用户信息
        User userInfo = userService.getById(userid);


        Map<String,Object> datamap=new HashMap<String, Object>();
        //数据类型User



        Map<String,Object> userMap=new HashMap<String, Object>();
        userMap.put("name",userInfo.getName());
        userMap.put("college",userInfo.getCollege());
        userMap.put("major",userInfo.getMajor());
        userMap.put("avatar",userInfo.getAvatar());

        Map<String,Object> stausMap=new HashMap<String, Object>();
        stausMap.put("streakDays",101);
        stausMap.put("dormTotal",101);
        stausMap.put("classTotal",101);
        stausMap.put("internTotal",101);

        //将用户信息存在map集合中
        //将考勤打卡等数据存在map集合中
        //将用户信息和考勤打卡数据进一步存在响应中的map集合中
        datamap.put("userInfo",userMap);
        datamap.put("stats",stausMap);
        return Result.success(datamap);
    }
}






















