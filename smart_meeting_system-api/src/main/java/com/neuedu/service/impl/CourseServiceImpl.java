package com.neuedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.pojo.Course;
import com.neuedu.pojo.Result;
import com.neuedu.service.CourseService;
import com.neuedu.mapper.CourseMapper;
import com.neuedu.vo.CourseVo;
import com.neuedu.vo.PageInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
* @author 86138
* @description 针对表【course(课程管理主表)】的数据库操作Service实现
* @createDate 2025-06-17 19:44:55
*/

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course>
    implements CourseService{

    @Autowired
    CourseMapper courseMapper;

    @Override
    public Result getCourses(PageInfoVo info) {
        IPage<Map> page = new Page<>(info.getPageNum(), info.getPageSize());

        courseMapper.selectMyPage(page,info.getKeyWords(),info.getSortBy());

        Map<String,Object> pageInfo = new HashMap<>();
        pageInfo.put("pageData",page.getRecords());

        pageInfo.put("pageNum",page.getCurrent());
        pageInfo.put("pageSize",page.getSize());
        pageInfo.put("totalSize", page.getTotal());
        Map<String,Object> data = new HashMap<>();
        data.put("pageInfo",pageInfo);
        return Result.success(data);
    }

   /* @Override
    public Result search(String name) {
        List<Map<String, Object>> courses = courseMapper.selectByCourseName(name);
        CourseVo[] data = new CourseVo[courses.size()];
        Arrays.setAll(data, i -> new CourseVo());

        for (int i = 0; i < courses.size(); i++) {
            Map<String, Object> map = courses.get(i);
            CourseVo courseVo = data[i];

            // 这里字段名要和你SQL语句中SELECT的列名一致
            Object idObj = map.get("id");
            Object nameObj = map.get("course_name");
            Object authorObj = map.get("author");
            Object descObj = map.get("description");

            if (idObj != null) {
                courseVo.setId(((Number) idObj).intValue());
            }
            if (nameObj != null) {
                courseVo.setName(nameObj.toString());
            }
            if (authorObj != null) {
                courseVo.setAuthor(authorObj.toString());
            }
            if (descObj != null) {
                courseVo.setDescription(descObj.toString());
            }
        }
        return Result.success(data);
    }*/

    @Override
    public Result sorted(String method) {
        List<Course> courses = courseMapper.selectList(null);
        CourseVo[] data = new CourseVo[courses.size()];
        Arrays.setAll(data,i->new CourseVo());
        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            CourseVo c = data[i];
            c.setId(Math.toIntExact(course.getId()));
            c.setAuthor(course.getAuthor());
            c.setName(course.getCourseName());
            c.setDescription(course.getDescription());
        }
        Arrays.sort(data, new Comparator<CourseVo>() {
            @Override
            public int compare(CourseVo o1, CourseVo o2) {
                if (method.equals("author")) {
                    return o1.getAuthor().compareTo(o2.getAuthor()); // 假设 author 是 String
                } else {
                    return o1.getName().compareTo(o2.getName());    // 按 name 字符串排序
                }
            }
        });

        return Result.success(data);
    }

    @Override
    public Result add(CourseVo vo) {
        Course course = new Course();
        BeanUtils.copyProperties(vo, course); // 只复制同名字段
        course.setCourseName(vo.getName()); // 单独处理字段名不一样的
        course.setIsDeleted(0);
        courseMapper.insert(course);
        return Result.success();
    }

    @Override
    public Result deleteCourse(long id) {
        System.out.println(id);
        Course course = courseMapper.selectById(id);
        if(course==null){
            return Result.error("课程不存在");
        }
        System.out.println("查询到的课程: " + course);
        int i = courseMapper.deleteById(id);
        System.out.println("影响行数: " + i);
        if(i>0) return Result.success();
        return Result.error("删除失败");
    }

    @Override
    public Result handleRefactor(CourseVo vo) {
        System.out.println("前端传来 ID：" + vo.getId());

        Course course = new Course();
        BeanUtils.copyProperties(vo, course);
        course.setCourseName(vo.getName());
        course.setId((long) vo.getId());

        int rows = courseMapper.updateById(course);
        if (rows == 0) {
            return Result.error("更新失败，课程可能不存在");
        }

        return Result.success();
    }

    @Override
    public Result clearIsDeleted() {
        courseMapper.deleteWhereIsDeleted();
        return Result.success();
    }


}




