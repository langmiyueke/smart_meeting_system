package com.neuedu;

import com.neuedu.utils.GenerateCode;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenerateCodeTest {

    @Test
    void testGenerateCode_LengthAndDigits() {
        String code = GenerateCode.generateCode();
        assertEquals(6, code.length(), "验证码应为6位长度");
        assertTrue(code.matches("\\d{6}"), "验证码应仅包含数字");
    }

    @Test
    void testGenerateCode_ValueRange() {
        for (int i = 0; i < 1000; i++) {
            String code = GenerateCode.generateCode();
            int num = Integer.parseInt(code);
            assertTrue(num >= 100000 && num <= 999999,
                    "生成的数字应在 100000 ~ 999999 之间");
        }
    }

    @Test
    void testGenerateCode_Randomness() {
        Set<String> codes = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String code = GenerateCode.generateCode();
            codes.add(code);
        }
        // 如果太少，说明重复了太多（非真正唯一，但应大概率避免重复）
        assertTrue(codes.size() > 950, "生成的验证码应具备一定的随机性");
    }
}