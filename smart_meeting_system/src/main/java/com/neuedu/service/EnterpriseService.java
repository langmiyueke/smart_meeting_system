package com.neuedu.service;

import com.neuedu.entity.Enterprises;
import com.neuedu.mapper.EnterprisesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EnterpriseService {
    @Autowired
    private EnterprisesMapper enterprisesMapper;

    public int addEnterprises(Enterprises enterprises) {

        // 设置用户标识为随机的八到十位随机数字与字母组合
        String random_enterprise_mark = generateRandomEnterpriseMark();
        enterprises.setEnterprise_mark(random_enterprise_mark);


        return enterprisesMapper.addEnterprises(enterprises);
    }

    public String generateRandomEnterpriseMark() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        int length = 8 + random.nextInt(3); // 随机生成8到10位长度
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
}
