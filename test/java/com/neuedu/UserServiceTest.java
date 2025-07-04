package com.neuedu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.neuedu.pojo.Result;
import com.neuedu.pojo.User;
import com.neuedu.service.UserService;
import com.neuedu.vo.VerifyCodeVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testLogin() throws JsonProcessingException {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        Result result = userService.login(user);
        assertNotNull(result);
    }

    @Test
    public void testSendVerificationCode() {
        String phoneNumber = "1234567890";
        Result result = userService.sendVerificationCode(phoneNumber);
        assertNotNull(result);
    }

    @Test
    public void testVerifyCode() {
        VerifyCodeVo verifyCodeVo = new VerifyCodeVo();
        verifyCodeVo.setPhoneNumber("1234567890");
        verifyCodeVo.setCode("123456");
        Result result = userService.verifyCode(verifyCodeVo);
        assertNotNull(result);
    }

    @Test
    public void testToRegister() {
        User user = new User();
        user.setUsername("newUser");
        user.setPassword("newPassword");
        Result result = userService.ToRegister(user);
        assertNotNull(result);
    }
}