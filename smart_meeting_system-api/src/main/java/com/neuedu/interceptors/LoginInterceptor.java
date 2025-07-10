package com.neuedu.interceptors;

import com.neuedu.utils.JwtUtil;
import com.neuedu.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        // 放行不需要登录验证的路径
        if (uri.startsWith("/user/login") || uri.startsWith("/user/register")
                || uri.startsWith("/user/verificationCode") || uri.startsWith("/user/verifyCode")) {
            return true;
        }
        //令牌验证
        String token = request.getHeader("Authorization");

        try {
            String redisToken = stringRedisTemplate.opsForValue().get(token);
            if (redisToken == null) {
                throw new Exception("令牌失效");
            }
            Map<String, Object> claims = JwtUtil.parseToken(token);
            ThreadLocalUtil.set(claims);
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清空ThreadLocal中的数据，防止内存泄露
        ThreadLocalUtil.remove();
    }
}
