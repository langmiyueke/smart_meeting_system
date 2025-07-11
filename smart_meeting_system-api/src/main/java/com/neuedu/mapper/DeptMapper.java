package com.neuedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neuedu.pojo.Dept;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DeptMapper extends BaseMapper<Dept> {

    // 递归查询子部门ID（用于级联删除）
    List<Integer> findChildIds(@Param("parentId") Integer parentId);

    // 分页查询（带条件）
    IPage<Map<String, Object>> selectDeptPage(
            IPage<Map<String, Object>> page,
            @Param("deptName") String deptName,
            @Param("status") String status
    );
}
