package com.neuedu.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neuedu.pojo.Course;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.vo.PageInfoVo;
import org.apache.ibatis.annotations.Param;

import java.util.Map;
import java.util.List;

/**
* @author 86138
* @description 针对表【course(课程管理主表)】的数据库操作Mapper
* @createDate 2025-06-17 19:44:55
* @Entity com.neuedu.pojo.Course
*/
public interface CourseMapper extends BaseMapper<Course> {

    List<Map<String, Object>> selectByCourseName(@Param("name") String name);

    IPage<Map> selectMyPage(IPage<Map> page,
                            @Param("keyWords") String keyWords,
                            @Param("sortBy") String sortBy);

    void deleteWhereIsDeleted();

}




