package com.neuedu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.pojo.Dept;
import com.neuedu.pojo.Result;
import com.neuedu.vo.DeptVo;
import com.neuedu.vo.PageInfoVo;

public interface DeptService extends IService<Dept> {

    Result getDeptTree();

    Result getDeptPage(PageInfoVo pageInfo);

    Result addDept(DeptVo deptVo);

    Result updateDept(DeptVo deptVo);

    Result deleteDept(Integer deptId);
}
