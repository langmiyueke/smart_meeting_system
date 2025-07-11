package com.neuedu.controller;

import com.neuedu.pojo.*;
import com.neuedu.pojo.Users;
import com.neuedu.mapper.UsersMapper;
import com.neuedu.service.EnterpriseService;
import com.neuedu.mapper.EnterprisesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class EnterprisesController {
    @Autowired
    EnterprisesMapper enterprisesMapper;
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    private UsersMapper usersMapper;

    //分页显示租户信息
    @RequestMapping("/getenterprises")
    public Map<String, Object> getEnterprises(@RequestParam(defaultValue = "1") int currentPage,
                                              @RequestParam(defaultValue = "10") int pageSize) {
        int offset = (currentPage - 1) * pageSize;
        List<Enterprises> enterprises = enterprisesMapper.getEnterprises(offset, pageSize);
        int total = enterprisesMapper.getTotalCount();

        Map<String, Object> result = new HashMap<>();
        result.put("data", enterprises);
        result.put("total", total);
        result.put("currentPage", currentPage);
        result.put("pageSize", pageSize);

        return result;
    }

    //查询租户信息
    @RequestMapping("/searchenterprises")
    public Map<String, Object> searchEnterprises( @RequestBody Enterprises enterprises,
                                                  @RequestParam(defaultValue = "1") int currentPage,
                                                  @RequestParam(defaultValue = "10") int pageSize) {
        int offset = (currentPage - 1) * pageSize;
        List<Enterprises> return_enterprises = enterprisesMapper.searchEnterprises(enterprises, offset, pageSize);
        int total = enterprisesMapper.getSearchCount(enterprises);

        Map<String, Object> result = new HashMap<>();
        result.put("data", return_enterprises);
        result.put("total", total);
        result.put("currentPage", currentPage);
        result.put("pageSize", pageSize);

        return result;
    }

    //删除租户信息同时删除该租户的用户
    @Transactional
    @RequestMapping("/delenterprises")
    public int delEnterprises(String enterprise_mark) {
        Enterprises enterprises =enterprisesMapper.getEnterprisesByEnterpriseMark(enterprise_mark);
        List<Integer> usersid=usersMapper.getUserByEnterpriseName(enterprises.getName());
        for(Integer userId:usersid){
            usersMapper.delUsers(userId);
            usersMapper.delUserInformation(userId);
        }
        return enterprisesMapper.delEnterprises(enterprise_mark);
    }

    //添加租户信息
    @Transactional
    @RequestMapping("/addenterprises")
    public int addEnterprises(@RequestBody Enterprises enterprises) {
        Users user = new Users();
        user.setEnterprise_name(enterprises.getName());
        user.setUsername(enterprises.getManager_username());
        user.setNickname(enterprises.getName());
        user.setPassword(enterprises.getEnterprise_mark());
        user.setState("正常");
        user.setRole("管理员");
        user.setCreate_at(Timestamp.valueOf(LocalDateTime.now()));
        usersMapper.addUsers(user);
        usersMapper.addUserInformation(user);
        return enterpriseService.addEnterprises(enterprises);
    }

    //修改租户信息
    @RequestMapping("/updateenterprises")
    public int updateEnterprises(@RequestBody Enterprises enterprises) {
        return enterprisesMapper.updateEnterprises(enterprises);
    }

    //通过租户标识获取租户信息
    @RequestMapping("/getenterprisebyenterprisemark")
    public Enterprises getEnterpriseByEnterpriseMark(String enterprise_mark){
        Enterprises enterprise = enterprisesMapper.getEnterprisesByEnterpriseMark(enterprise_mark);
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
    @RequestMapping("/enterprisesname")
    public List<String> getEnterprisesName(){
        return enterprisesMapper.getEnterprisesName();
    }

    //通过名称查询租户
    @RequestMapping("/getenterprisesbyname")
    public List<String> getEnterprisesByName(String name){
        return enterprisesMapper.getEnterprisesByName(name);
    }

}
