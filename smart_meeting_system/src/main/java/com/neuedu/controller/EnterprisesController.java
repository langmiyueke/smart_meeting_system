package com.neuedu.controler;

import com.neuedu.entity.Enterprises;
import com.neuedu.service.EnterpriseService;
import com.neuedu.mapper.EnterprisesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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

    //删除租户信息
    @RequestMapping("/delenterprises")
    public int delEnterprises(String enterprise_mark) {
        return enterprisesMapper.delEnterprises(enterprise_mark);
    }

    //添加租户信息
    @RequestMapping("/addenterprises")
    public int addEnterprises(@RequestBody Enterprises enterprises) {
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

}
