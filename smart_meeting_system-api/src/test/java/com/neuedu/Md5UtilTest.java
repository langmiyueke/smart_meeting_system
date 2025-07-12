package com.neuedu;

import com.neuedu.utils.Md5Util;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Md5UtilTest {

    @Test
    void testGetMD5String_NotNull() {
        String input = "test123";
        String md5 = Md5Util.getMD5String(input);
        assertNotNull(md5);
        assertEquals(32, md5.length(), "MD5应为32位");
        assertTrue(md5.matches("[a-f0-9]{32}"), "MD5应为小写十六进制字符串");
    }

    @Test
    void testGetMD5String_Consistency() {
        String input = "password";
        String md5_1 = Md5Util.getMD5String(input);
        String md5_2 = Md5Util.getMD5String(input);
        assertEquals(md5_1, md5_2, "相同字符串的MD5值应一致");
    }

    @Test
    void testCheckPassword_Success() {
        String input = "abc123";
        String md5 = Md5Util.getMD5String(input);
        assertTrue(Md5Util.checkPassword("abc123", md5));
    }

    @Test
    void testCheckPassword_Fail() {
        String input = "abc123";
        String md5 = Md5Util.getMD5String(input);
        assertFalse(Md5Util.checkPassword("wrongpass", md5));
    }

    @Test
    void testDifferentInputs_GiveDifferentHashes() {
        String md5a = Md5Util.getMD5String("abc");
        String md5b = Md5Util.getMD5String("abcd");
        assertNotEquals(md5a, md5b);
    }
}