package com.neuedu;

import com.neuedu.pojo.Course;
import com.neuedu.pojo.Result;
import com.neuedu.service.CourseService;
import com.neuedu.vo.CourseVo;
import com.neuedu.vo.PageInfoVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Test
    public void testGetCourses() {
        PageInfoVo pageInfo = new PageInfoVo();
        pageInfo.setPageNum(1);
        pageInfo.setPageSize(10);
        Result result = courseService.getCourses(pageInfo);
        assertNotNull(result);
    }

    @Test
    public void testAddCourse() {
        CourseVo courseVo = new CourseVo();
        courseVo.setName("Test Course");
        courseVo.setDescription("This is a test course.");
        Result result = courseService.add(courseVo);
        assertNotNull(result);
    }

    @Test
    public void testDeleteCourse() {
        long courseId = 1L; // 假设存在的课程ID
        Result result = courseService.deleteCourse(courseId);
        assertNotNull(result);
    }
}