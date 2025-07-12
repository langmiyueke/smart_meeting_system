package com.neuedu.controller;

import com.neuedu.pojo.Enterprise;
import com.neuedu.pojo.User;
import com.neuedu.mapper.EmployeeMapper;
import com.neuedu.mapper.UserMapper;
import com.neuedu.service.EnterpriseService;
import com.neuedu.mapper.EnterpriseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("enterpriseManagement")
@Transactional
@CrossOrigin
public class EnterpriseController {
    @Autowired
    EnterpriseMapper enterpriseMapper;
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private UserMapper userMapper;

    //分页显示租户信息
    @RequestMapping("/getenterprise")
    public Map<String, Object> getEnterprise(@RequestParam(defaultValue = "1") int currentPage,
                                             @RequestParam(defaultValue = "10") int pageSize) {


        if (currentPage <= 0 || pageSize <= 0) {
            throw new IllegalArgumentException("页码和每页大小必须是正整数");
        }

        int offset = (currentPage - 1) * pageSize;
        List<Enterprise> enterprises = enterpriseMapper.getEnterprise(offset, pageSize);
        int total = enterpriseMapper.getTotalCount();

        Map<String, Object> result = new HashMap<>();
        result.put("data", enterprises);
        result.put("total", total);
        result.put("currentPage", currentPage);
        result.put("pageSize", pageSize);
        return result;
    }

    //查询租户信息
    @RequestMapping("/searchenterprise")
    public Map<String, Object> searchEnterprise( @RequestBody Enterprise enterprise,
                                                 @RequestParam(defaultValue = "1") int currentPage,
                                                 @RequestParam(defaultValue = "10") int pageSize) {
        int offset = (currentPage - 1) * pageSize;
        List<Enterprise> enterprises = enterpriseMapper.searchEnterprise(enterprise, offset, pageSize);
        int total = enterpriseMapper.getSearchCount(enterprise);

        Map<String, Object> result = new HashMap<>();
        result.put("data", enterprises);
        result.put("total", total);
        result.put("currentPage", currentPage);
        result.put("pageSize", pageSize);

        return result;
    }

    //删除租户信息同时删除该租户的所有员工信息与该租户的账户信息
    @Transactional
    @RequestMapping("/delenterprise")
    public int delEnterprise(String enterprise_mark) {
        Enterprise enterprise = enterpriseMapper.getEnterpriseByEnterpriseMark(enterprise_mark);
        Long userid=enterprise.getId();
        userMapper.deleteUser(userid);
        List<Integer> employeeid= employeeMapper.getEmployeeIdByEnterpriseName(enterprise.getName());
        for(Integer employeeId:employeeid){
            employeeMapper.delEmployee(employeeId);
        }
        return enterpriseMapper.delEnterprise(enterprise_mark);
    }

    //添加租户信息
    @Transactional
    @RequestMapping("/addenterprise")
    public int addEnterprise(@RequestBody Enterprise enterprise) {
        String manager_username=enterprise.getManager_username();
        if (userMapper.getCount(manager_username)==1){
            return -1;
        }
        else{
            enterpriseService.addEnterprises(enterprise);
            User user = new User();
            user.setUsername(enterprise.getManager_username());
            user.setPassword(enterprise.getEnterprise_mark());
            user.setRole("管理员");
            user.setEnterpriseName(enterprise.getName());
            user.setEnterprisePhone(enterprise.getPhone());
            return userMapper.insertUser(user);
        }
    }

    //修改租户信息
    @RequestMapping("/updateenterprise")
    public int updateEnterprises(@RequestBody Enterprise enterprise) {

        User user = new User();
        user.setUsername(enterprise.getManager_username());
        user.setEnterpriseName(enterprise.getName());
        user.setEnterprisePhone(enterprise.getPhone());
        userMapper.updateUser(user);

        return enterpriseMapper.updateEnterprise(enterprise);
    }

    //通过租户标识获取租户信息
    @RequestMapping("/getenterprisebyenterprisemark")
    public Enterprise getEnterpriseByEnterpriseMark(String enterprise_mark){
        Enterprise enterprise = enterpriseMapper.getEnterpriseByEnterpriseMark(enterprise_mark);
        // 确保图标数据完整返回
        if (enterprise != null && enterprise.getEnterprise_icon() != null) {
            // 如果数据库存储的是纯Base64数据，添加前缀
            if (!enterprise.getEnterprise_icon().startsWith("data:image")) {
                enterprise.setEnterprise_icon("data:image/png;base64," + enterprise.getEnterprise_icon());
            }
        }
        return enterprise;
    }

    //获取租户名称
    @RequestMapping("/enterprisename")
    public List<String> getEnterprisesName(){
        return enterpriseMapper.getEnterpriseName();
    }

    //通过名称查询租户
    @RequestMapping("/getenterprisebyname")
    public List<String> getEnterprisesByName(String name){
        return enterpriseMapper.getEnterpriseByName(name);
    }

}
