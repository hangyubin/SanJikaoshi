package com.smartexam.backend.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密工具类
 */
public class PasswordUtils {
    
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    /**
     * 加密密码
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encodePassword(String password) {
        return encoder.encode(password);
    }
    
    /**
     * 验证密码
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
    
    /**
     * 判断密码是否已加密
     * @param password 密码
     * @return 是否已加密
     */
    public static boolean isEncoded(String password) {
        // BCrypt加密后的密码以$2a$开头
        return password != null && password.startsWith("$2a$");
    }
}


