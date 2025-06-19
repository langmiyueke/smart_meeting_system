package com.neuedu.mapper;

import com.neuedu.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;

/**
* @author 86138
* @description 针对表【user】的数据库操作Mapper
* @createDate 2025-06-16 12:29:19
* @Entity com.neuedu.pojo.User
*/
public interface UserMapper extends BaseMapper<User> {
    @Insert("INSERT INTO user " +
            "(id, username, password, role, enterprise_name, enterprise_phone) " +
            "VALUES (#{id}, #{username}, #{password}, #{role}, #{enterpriseName}, #{enterprisePhone})")
    int ToRegister(User user);

}




