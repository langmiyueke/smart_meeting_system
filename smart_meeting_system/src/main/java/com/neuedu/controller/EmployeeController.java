package com.neuedu.controller;

import com.neuedu.entity.Employee;
import com.neuedu.entity.SearchEmployeeRequest;
import com.neuedu.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class EmployeeController {
    @Autowired
    EmployeeMapper employeeMapper;

    //显示员工信息
    @Transactional
    @RequestMapping("/getemployee")
    public Map<String, Object> getEmployeeByPage(@RequestParam String enterprise_name,
                                              @RequestParam(defaultValue = "1") int currentPage,
                                              @RequestParam(defaultValue = "10") int pageSize) {

        if (enterprise_name == null || enterprise_name.trim().isEmpty()) {
            throw new IllegalArgumentException("Enterprise name cannot be empty");
        }
        if (currentPage <= 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Page parameters must be positive");
        }

        int offset = (currentPage - 1) * pageSize;
        List<Employee> employees = employeeMapper.getEmployeeByPage(enterprise_name,offset, pageSize);
        int total = employeeMapper.getTotalCount(enterprise_name);

        // 格式化日期
        for (Employee employee : employees) {
            Timestamp create_at = employee.getCreate_at();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            employee.setUpdate_create_at(sdf.format(create_at));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", employees);
        result.put("total", total);
        result.put("currentPage", currentPage);
        result.put("pageSize", pageSize);

        return result;
    }

    //查询员工信息
    @Transactional
    @RequestMapping("/searchemployee")
    public Map<String, Object> searchEmployee(
            @RequestBody SearchEmployeeRequest request,
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize) {

        int offset = (currentPage - 1) * pageSize;
        List<Employee> employees = employeeMapper.searchEmployeeByPage(request, offset, pageSize);
        int total = employeeMapper.getSearchCount(request);

        // 格式化日期
        for (Employee employee : employees) {
            Timestamp create_at = employee.getCreate_at();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            employee.setUpdate_create_at(sdf.format(create_at));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", employees);
        result.put("total", total);
        result.put("currentPage", currentPage);
        result.put("pageSize", pageSize);

        return result;
    }

    //删除员工信息
    @Transactional
    @RequestMapping("/delemployee")
    public int delEmployee(int id) {
        return employeeMapper.delEmployee(id);
    }

    //添加员工信息
    @Transactional
    @RequestMapping("/addemployee")
    public int addEmployee(@RequestBody Employee employee) {
        Timestamp create_time= employee.getCreate_at();
        employee.setCreate_at(Timestamp.valueOf(LocalDateTime.now()));
        return employeeMapper.addEmployee(employee);
    }

    //修改员工信息
    @Transactional
    @RequestMapping("/updateemployee")
    public int updateEmployee(@RequestBody Employee employee) {
        return employeeMapper.updateEmployee(employee);
    }

    //通过id查找员工信息
    @RequestMapping("/getemployeebyid")
    public Employee getEmployeeById(int id){
        return employeeMapper.getEmployee(id);
    }
}
