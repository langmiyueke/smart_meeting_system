package com.neuedu.service;

import com.neuedu.pojo.Course;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.pojo.Result;
import com.neuedu.vo.CourseVo;
import com.neuedu.vo.PageInfoVo;

/**
* @author 86138
* @description 针对表【course(课程管理主表)】的数据库操作Service
* @createDate 2025-06-17 19:44:55
*/
public interface CourseService extends IService<Course> {

    Result getCourses(PageInfoVo pageInfo);

    /*Result search(String name);*/

    Result sorted(String method);

    Result add(CourseVo vo);

    Result deleteCourse(long id);

    Result handleRefactor(CourseVo vo);

    Result clearIsDeleted();

}
