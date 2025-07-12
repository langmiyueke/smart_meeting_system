package com.neuedu;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.neuedu.utils.JwtUtil;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @Test
    void testGenAndParseToken_Success() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 123);
        claims.put("role", "admin");

        // Act
        String token = JwtUtil.genToken(claims);
        assertNotNull(token);

        Map<String, Object> parsed = JwtUtil.parseToken(token);

        // Assert
        assertEquals(123, parsed.get("userId"));
        assertEquals("admin", parsed.get("role"));
    }

    @Test
    void testParseToken_InvalidToken() {
        String fakeToken = "invalid.token.value";

        assertThrows(Exception.class, () -> {
            JwtUtil.parseToken(fakeToken);
        });
    }

    @Test
    void testParseToken_Expired() throws InterruptedException {
        // 自定义一个只活 1ms 的 token 来测试过期
        String shortToken = JWT.create()
                .withClaim("claims", Map.of("test", "expired"))
                .withExpiresAt(new Date(System.currentTimeMillis() + 1))
                .sign(Algorithm.HMAC256("neuedu"));

        Thread.sleep(10); // 确保已过期

        assertThrows(Exception.class, () -> JwtUtil.parseToken(shortToken));
    }
}