package com.neuedu.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuedu.pojo.Result;
import com.neuedu.pojo.User;
import com.neuedu.service.UserService;
import com.neuedu.mapper.UserMapper;
import com.neuedu.utils.GenerateCode;
import com.neuedu.utils.JwtUtil;
import com.neuedu.utils.Md5Util;
import com.neuedu.vo.VerifyCodeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
* @author 86138
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-06-16 12:29:19
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    @Autowired
    UserMapper userMapper;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result login(User user) throws JsonProcessingException {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        User loginUser = userMapper.selectOne(wrapper);

        if (loginUser == null) {
            return Result.error("用户不存在");
        }

        if (StringUtils.isEmpty(loginUser.getPassword()) ||
                !Md5Util.getMD5String(user.getPassword()).equals(loginUser.getPassword())) {
            return Result.error("密码错误");
        }

        Map<String, Object> claims =  Map.of("id",loginUser.getId(),"username",loginUser.getUsername());

        String token = JwtUtil.genToken(claims);
        stringRedisTemplate.opsForValue().set(token, token, 1, TimeUnit.HOURS);

        Map<String, String> data = new HashMap<>();
        data.put("token", token);

        return Result.success(data);
    }

    @Override
    public Result sendVerificationCode(String phoneNumber) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10)); // 每位是0-9的随机数字
        }
        System.out.println(code);

        redisTemplate.opsForValue().set(phoneNumber, code.toString(), 1, TimeUnit.MINUTES);
        return Result.success(code.toString());
    }

    @Override
    public Result verifyCode(VerifyCodeVo verifyCodeVo) {
        String token = stringRedisTemplate.opsForValue().get(verifyCodeVo.getPhoneNumber());
        if (token == null) {
            return Result.error("验证码已过期");
        }
        System.out.println(token);
        if (token.equals(verifyCodeVo.getCode())) {
            Boolean isValid = true;
            return Result.success(isValid);
        }
        return Result.error("验证码错误");
    }

    @Override
    public Result ToRegister(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername());
        Long cnt = userMapper.selectCount(queryWrapper);
        if (cnt > 0) {
            return Result.error("用户已存在");
        }
        String password = user.getPassword();
        String md5String = Md5Util.getMD5String(password);
        user.setPassword(md5String);
        userMapper.insert(user);
        return Result.success();
    }


}




