package com.neuedu.utils;

import java.util.Map;

@SuppressWarnings("all")
public class ThreadLocalUtil {
    //提供ThreadLocal对象,
    private static final ThreadLocal THREAD_LOCAL = new ThreadLocal();

    //根据键获取值
    public static <T> T get(){
        return (T) THREAD_LOCAL.get();
    }

    //存储键值对
    public static void set(Object value){
        THREAD_LOCAL.set(value);
    }

    //清除ThreadLocal 防止内存泄漏
    public static void remove(){
        THREAD_LOCAL.remove();
    }

    public static Integer getUserId() {
        Map<String, Object> map = get();
        Integer id = (Integer) map.get("id");
        return id;
    }

    public static String getUsername() {
        Map<String, Object> map = get();
        String username = (String) map.get("username");
        return username;
    }
}
