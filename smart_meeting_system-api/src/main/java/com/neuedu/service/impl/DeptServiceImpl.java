package com.neuedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.mapper.DeptMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.pojo.Dept;
import com.neuedu.pojo.Result;
import com.neuedu.service.DeptService;
import com.neuedu.vo.DeptVo;
import com.neuedu.vo.PageInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept>
    implements DeptService {
    @Autowired
    DeptMapper deptMapper;

    @Override
    public Result getDeptTree() {
        List<Dept> allDepts = deptMapper.selectList(new QueryWrapper<Dept>().select("*"));
        return Result.success(allDepts);
    }


    @Override
    public Result getDeptPage(PageInfoVo pageInfo) {
        IPage<Map<String, Object>> page = new Page<>(
                pageInfo.getPageNum(),
                pageInfo.getPageSize()
        );

        deptMapper.selectDeptPage(
                page,
                pageInfo.getKeyWords(),
                pageInfo.getStatus()
        );

        // 转换下划线字段名为驼峰
        List<Map<String, Object>> camelCaseRecords = page.getRecords().stream()
                .map(record -> {
                    Map<String, Object> newRecord = new HashMap<>();
                    record.forEach((key, value) -> {
                        String camelKey = toCamelCase(key); // 实现下划线转驼峰方法
                        newRecord.put(camelKey, value);
                    });
                    return newRecord;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", camelCaseRecords);
        result.put("total", page.getTotal());
        return Result.success(result);
    }
    // 下划线转驼峰工具方法
    private String toCamelCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        String[] parts = str.split("_");
        return Arrays.stream(parts)
                .map(word -> word.isEmpty() ? "" :
                        parts[0].equals(word) ? word.toLowerCase() : // 首单词全部小写
                                word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase()
                )
                .collect(Collectors.joining(""));
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addDept(DeptVo deptVo) {
        // 校验部门名称唯一性
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<Dept>()
                .eq(Dept::getDeptName, deptVo.getDeptName()));
        if (count > 0) {
            return Result.error("部门名称已存在");
        }

        Dept dept = new Dept();
        BeanUtils.copyProperties(deptVo, dept);
        dept.setStatus(deptVo.getStatus());
        baseMapper.insert(dept);
        return Result.success(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateDept(DeptVo deptVo) {
        // 禁止设置父部门为自身或子部门
        if (deptVo.getDeptId().equals(deptVo.getParentId())) {
            return Result.error("父部门不能是自身");
        }

        // 检查是否设置子部门为父部门
        List<Integer> childIds = deptMapper.findChildIds(deptVo.getDeptId());
        if (childIds.contains(deptVo.getParentId())) {
            return Result.error("禁止设置子部门为父部门");
        }

        Dept dept = new Dept();
        BeanUtils.copyProperties(deptVo, dept);
        dept.setStatus(deptVo.getStatus());
        baseMapper.updateById(dept);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteDept(Integer deptId) {
        List<Integer> deleteIds = new ArrayList<>();
        deleteIds.add(deptId);
        deleteIds.addAll(deptMapper.findChildIds(deptId));

        if (baseMapper.deleteBatchIds(deleteIds) > 0) {
            return Result.success();
        }
        return Result.error("删除失败");
    }
}
