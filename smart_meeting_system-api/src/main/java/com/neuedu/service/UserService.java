package com.neuedu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.neuedu.pojo.Result;
import com.neuedu.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.vo.VerifyCodeVo;

/**
* @author 86138
* @description 针对表【user】的数据库操作Service
* @createDate 2025-06-16 12:29:19
*/
public interface UserService extends IService<User> {

    Result login(User user) throws JsonProcessingException;

    Result sendVerificationCode(String phoneNumber);

    public Result verifyCode(VerifyCodeVo verifyCodeVo);

    Result ToRegister(User user);

}
