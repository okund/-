package com.educheck.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.educheck.entity.UserFace;

public interface UserFaceService extends IService<UserFace>{

        UserFace getUserFace(Long userId);

}
