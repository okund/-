package com.educheck.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.educheck.entity.Course;
import com.educheck.mapper.CourseMapper;
import com.educheck.service.CourseService;
import org.springframework.stereotype.Service;

/**
 * zhang26/9/11 v1
 */
@Service

public class CourseServiceImpl
        extends ServiceImpl<CourseMapper, Course>
        implements CourseService {
}
