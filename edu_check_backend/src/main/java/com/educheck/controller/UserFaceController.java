package com.educheck.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.educheck.common.Result;
import com.educheck.common.TokenContextHolder;
import com.educheck.dto.FaceRegisterRequest;
import com.educheck.entity.UserFace;
import com.educheck.service.UserFaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/face")
@RequiredArgsConstructor
@Tag(name = "人脸管理", description = "人脸录入与验证接口")
public class UserFaceController {

    private final UserFaceService userFaceService;
    private final TokenContextHolder tokenContextHolder;

    /**
     *
     * 获取人脸录入状态
     */

    @GetMapping("/status")
    @Operation(summary = "获取人脸录入状态")
    public Result<Map<String, Object>> status() {

        Long userId = tokenContextHolder.requireCurrentUserId();
        UserFace userFace = userFaceService.getUserFace(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("registered", userFace != null);
        if (userFace != null) {
            data.put("version", userFace.getVersion());
            data.put("registeredAt", userFace.getCreatedAt());
            data.put("updatedAt", userFace.getUpdatedAt());
        }

        return Result.success(data);


    }

    @Value("${face.upload-dir:uploads/face}")
    private String faceUploadDir;

    @PostMapping("/register")
    @Operation(summary = "人脸录入更新功能")
    public Result<Map<String, Object>> register(@RequestBody FaceRegisterRequest request) {
        Long userId = tokenContextHolder.requireCurrentUserId();
        if (request.getImage() == null || request.getImage().isEmpty()) {
            return Result.error("人脸不能为空");
        }
        try {
            // 1. 改成Paths.get，变量名用faceUploadDir（@Value定义的变量）
            Path uploadDirPath = Paths.get(faceUploadDir);
            if (!Files.exists(uploadDirPath)) {
                Files.createDirectories(uploadDirPath);
            }

            String format = request.getFormat() != null ? request.getFormat() : "jpg";
            // 定义在try大括号最外层，保证后面可以访问
            String fileName = "face_" + userId + "_" + UUID.randomUUID().toString().substring(0, 8) + "." + format;
            Path filePath = uploadDirPath.resolve(fileName);
            byte[] imageBytes = Base64.getDecoder().decode(request.getImage());
            Files.write(filePath, imageBytes);

            // existing定义在try大括号最外层
            UserFace existing = userFaceService.getUserFace(userId);
            if (existing != null) {
                try {
                    Path oldPath = Paths.get(faceUploadDir, existing.getFaceUrl());
                    Files.deleteIfExists(oldPath);
                } catch (IOException e) {
                    log.warn("删除旧人脸图片失败: {}", e.getMessage());
                }
                existing.setFaceUrl(fileName);
                existing.setVersion(existing.getVersion() != null ? existing.getVersion() + 1 : 2);
                existing.setRegistered(1);
                existing.setUpdatedAt(LocalDateTime.now());
                userFaceService.updateById(existing);
                log.info("用户{}人脸更新成功(version={})", userId, existing.getVersion());
            } else {
                UserFace userFace = new UserFace();
                userFace.setUserId(userId);
                userFace.setFaceUrl(fileName);
                userFace.setRegistered(1);
                userFace.setVersion(1);
                userFaceService.save(userFace);
                log.info("用户{}人脸首次录入成功", userId);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("registered", true);
            data.put("message", existing != null ? "人脸更新成功" : "录入人脸成功");
            return Result.success(data);
        } catch (IOException e) {
            log.error("保存人脸失败: userId={}", userId, e);
            return Result.error("人脸图片保存失败");
        }
    }
}
   // UserFace userFace=userFaceService.getUserFace(userId);

    //String format = request.getFormat()==null?"jpg":request.getFormat();

    //if (!Files.exists(Paths.get(uploadDir))){
        //try {
            //Files.createDirectories(Paths.get(uploadDir));
        //}catch (IOException e){

            //return Result.error("上传文件路径不存在且创建异常");
        //}
    //}

    //String fileName="face_"+userId+"_"+ UUID.randomUUID().toString().substring(0,8)+"."+format;

    //byte[] imageBytes= Base64.getDecoder().decode(faceImage);

    //try {
        //if (userFace==null||userFace.getFaceUrl()==null||userFace.getFaceUrl().isEmpty()){
            //Files.write(Paths.get(uploadDir+"\\"+fileName),imageBytes);
            //userFace=new UserFace();
            //userFace.setVersion(1);
            //userFace.setRegistered(1);
            //userFace.setFaceUrl(uploadDir+"\\"+fileName);
            //boolean result1=userFaceService.save(userFace);

            //Map<String,Object> data=new HashMap<>(String,Object)();
            //data.put("registered",true);
            //data.put("message","人脸注册成功");
            //return Result.success(data);



    //}else{
            //String oldImage = userFace.getFaceUrl();
            //Files.delete(Paths.get(oldImage));
            //Files.write(Paths.get(uploadDir + "\\" + fileName), imageBytes);

            //userFace.setVersion(1);
            //userFace.setRegistered(1);
            //userFace.setFaceUrl(uploadDir + "\\" + fileName);
            //userFace.setUserId(userId);
            //userFace.setUpdatedAt(LocalDateTime.now());
            //boolean result1 = userFaceService.updateById(userFace);
        //}
        //Map<String,Object> data=new HashMap<>(String,Object)();
        //data.put("registered",true);
        //data.put("message","人脸注册成功");
        //return Result.success(data);
    //}catch (IOException e) {
        //log.error("保持人脸图片失败：userId={}", userId, e);
        //return Result.error("数据传输失败");
    //}
//}`