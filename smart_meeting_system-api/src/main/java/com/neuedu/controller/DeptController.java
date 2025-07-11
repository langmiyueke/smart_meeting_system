package com.neuedu.controller;


import com.neuedu.pojo.Result;
import com.neuedu.service.DeptService;
import com.neuedu.vo.DeptVo;
import com.neuedu.vo.PageInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("dept")
@CrossOrigin
public class DeptController {
    @Autowired
    DeptService deptService;
    // 获取部门树形结构
    @GetMapping("/tree")
    public Result getDeptTree() {
        return deptService.getDeptTree();
    }
    // 分页查询部门
    @PostMapping("/page")
    public Result getDeptPage(@RequestBody PageInfoVo pageInfo) {
        return deptService.getDeptPage(pageInfo);
    }
    // 添加部门
    @PostMapping
    public Result addDept(@RequestBody DeptVo deptVo) {
        return deptService.addDept(deptVo);
    }

    // 修改部门
    @PutMapping
    public Result updateDept(@RequestBody DeptVo deptVo) {
        return deptService.updateDept(deptVo);
    }

    // 删除部门
    @DeleteMapping("/{deptId}")
    public Result deleteDept(@PathVariable Integer deptId) {
        return deptService.deleteDept(deptId);
    }
}
