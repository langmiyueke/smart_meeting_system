package com.neuedu.controller;

import com.neuedu.pojo.Result;
import com.neuedu.service.CourseService;
import com.neuedu.vo.CourseVo;
import com.neuedu.vo.PageInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("course")
@CrossOrigin
public class CourseController {
    @Autowired
    CourseService courseService;

    @PostMapping("getCourses")
    public Result getCourses(@RequestBody PageInfoVo pageInfo){
        Result result = courseService.getCourses(pageInfo);
        return result;
    }

/*    @GetMapping("search")
    Result search(String name){
        Result result = courseService.search(name);
        return result;
    }*/

    @GetMapping("sorted")
    Result sorted(String method){
        Result result = courseService.sorted(method);
        return result;
    }

    @PostMapping("handleAdd")
    private Result add(@RequestBody CourseVo vo){

        Result result = courseService.add(vo);
     return result;
    }

    @GetMapping("deleteCourse")
    private Result deleteCourse(long id){
        Result result = courseService.deleteCourse(id);
        return result;
    }

    @PutMapping("handleRefactor")
    private Result handleRefactor(@RequestBody CourseVo vo){
        Result result = courseService.handleRefactor(vo);
        return result;
    }

    @GetMapping("clearIsDeleted")
    private  Result clearIsDeleted(){
        Result result = courseService.clearIsDeleted();
        return result;
    }

}
