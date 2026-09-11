package com.educheck.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.educheck.entity.UserFace;
import com.educheck.mapper.UserFaceMapper;
import com.educheck.service.UserFaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserFaceServiceImpl2 extends ServiceImpl<UserFaceMapper,UserFace> implements UserFaceService {
    @Override
    public UserFace getUserFace(Long userId) {
        try {

            Object val;
            return lambdaQuery()
                    .eq(UserFace::getUserId,userId)
                    .eq(UserFace::getRegistered,val=1)
                    .one();
        }catch (DataAccessException e){
            log.warn("查询用户人脸失败:{}"+e.getMessage());
            return null;
        }
    }
}
