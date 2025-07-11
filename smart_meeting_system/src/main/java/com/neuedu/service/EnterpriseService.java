package com.neuedu.service;

import com.neuedu.entity.Enterprise;
import com.neuedu.mapper.EnterpriseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EnterpriseService {
    @Autowired
    private EnterpriseMapper enterpriseMapper;

    public int addEnterprises(Enterprise enterprise) {

        // 设置用户标识为随机的八到十位随机数字与字母组合
        String random_enterprise_mark = generateRandomEnterpriseMark();
        enterprise.setEnterprise_mark(random_enterprise_mark);

        return enterpriseMapper.addEnterprise(enterprise);
    }

    public String generateRandomEnterpriseMark() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz123456789";//为了方便分分大写的o与0，大写的i与小写的l，将不生成0和l
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