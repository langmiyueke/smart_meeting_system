package com.neuedu.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.mapper.DeptMapper;
import com.neuedu.pojo.Dept;
import com.neuedu.pojo.Result;
import com.neuedu.vo.DeptVo;
import com.neuedu.vo.PageInfoVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DeptServiceImplTest {

    @Autowired
    private DeptServiceImpl deptService;

    @MockBean
    private DeptMapper deptMapper;

    @Test
    void testDeleteDept_Success() {
        // Arrange
        Integer deptId = 1;
        List<Integer> childIds = List.of(2, 3);
        List<Integer> allIds = List.of(1, 2, 3);

        when(deptMapper.findChildIds(deptId)).thenReturn(childIds);
        when(deptMapper.deleteBatchIds(allIds)).thenReturn(3);

        // Act
        Result result = deptService.deleteDept(deptId);

        // Assert
        assertEquals(0, result.getCode());
        assertEquals("操作成功", result.getMessage());
    }

    @Test
    void testDeleteDept_Fail() {
        // Arrange
        Integer deptId = 1;
        when(deptMapper.findChildIds(deptId)).thenReturn(List.of());
        when(deptMapper.deleteBatchIds(List.of(deptId))).thenReturn(0);

        // Act
        Result result = deptService.deleteDept(deptId);

        // Assert
        assertEquals(1, result.getCode());
        assertEquals("删除失败", result.getMessage());
    }

    @Test
    void testAddDept_NameConflict() {
        DeptVo deptVo = new DeptVo();
        deptVo.setDeptName("研发部");

        // 模拟数据库中已有相同名称
        when(deptMapper.selectCount(any())).thenReturn(1L);

        Result result = deptService.addDept(deptVo);

        assertEquals(1, result.getCode());
        assertEquals("部门名称已存在", result.getMessage());
    }

    @Test
    void testUpdateDept_SetParentToSelf() {
        DeptVo deptVo = new DeptVo();
        deptVo.setDeptId(1);
        deptVo.setParentId(1);

        Result result = deptService.updateDept(deptVo);

        assertEquals(1, result.getCode());
        assertEquals("父部门不能是自身", result.getMessage());
    }

    @Test
    void testUpdateDept_SetChildAsParent() {
        DeptVo deptVo = new DeptVo();
        deptVo.setDeptId(1);
        deptVo.setParentId(3);

        when(deptMapper.findChildIds(1)).thenReturn(List.of(3, 4));

        Result result = deptService.updateDept(deptVo);

        assertEquals(1, result.getCode());
        assertEquals("禁止设置子部门为父部门", result.getMessage());
    }
    @Test
    void testGetDeptTree() {
        // Arrange
        List<Dept> deptList = List.of(
                new Dept(1, 0, "公司", 1, "张三", "123", "a@a.com", "正常", null),
                new Dept(2, 1, "研发部", 2, "李四", "456", "b@b.com", "正常", null)
        );

        when(deptMapper.selectList(any())).thenReturn(deptList);

        // Act
        Result result = deptService.getDeptTree();

        // Assert
        assertEquals(0, result.getCode());
        assertTrue(result.getData() instanceof List);
        assertEquals(2, ((List<?>) result.getData()).size());
    }

    @Test
    void testGetDeptPage() {
        // Arrange
        PageInfoVo pageInfo = new PageInfoVo();
        pageInfo.setPageNum(1);
        pageInfo.setPageSize(10);
        pageInfo.setKeyWords("研发");
        pageInfo.setStatus("正常");

        // 模拟分页结果
        Page<Map<String, Object>> mockPage = new Page<>();
        Map<String, Object> row = new HashMap<>();
        row.put("dept_id", 1);
        row.put("dept_name", "研发部");
        row.put("parent_id", 0);
        mockPage.setRecords(List.of(row));
        mockPage.setTotal(1);

        // Mockito 处理分页引用传参（使用 doAnswer 或 thenAnswer）
        doAnswer(invocation -> {
            IPage<Map<String, Object>> page = invocation.getArgument(0);
            page.setRecords(mockPage.getRecords());
            page.setTotal(mockPage.getTotal());
            return null;
        }).when(deptMapper).selectDeptPage(any(), eq("研发"), eq("正常"));

        // Act
        Result result = deptService.getDeptPage(pageInfo);

        // Assert
        assertEquals(0, result.getCode());
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals(1L, data.get("total"));
        List<?> records = (List<?>) data.get("data");
        assertEquals("deptId", records.get(0).toString().contains("deptId") ? "deptId" : "missing");
    }
}
