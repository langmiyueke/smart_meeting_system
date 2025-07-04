package com.neuedu.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.neuedu.pojo.Result;
import com.neuedu.pojo.User;
import com.neuedu.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import com.neuedu.vo.VerifyCodeVo;
@RestController
@RequestMapping("user")
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userServiceImpl;
    @Autowired
    StringRedisTemplate redisTemplate;

    @PostMapping("login")
    public Result login(@RequestBody User user) throws JsonProcessingException {
        Result result = userServiceImpl.login(user);
        return result;
    }



    @GetMapping("verificationCode")
    public Result sendVerificationCode(@Param("phoneNumber") String phoneNumber) {
        Result result = userServiceImpl.sendVerificationCode(phoneNumber);
        return result;
    }

    @PostMapping("verifyCode")
    public Result verifyCode(@RequestBody VerifyCodeVo vo) {
        Result result = userServiceImpl.verifyCode(vo);
        return result;
    }

    @PostMapping("register")
    public Result ToRegister(@RequestBody User user) {
        Result result = userServiceImpl.ToRegister(user);
        return result;
    }


}
