package com.learn.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.Data;
@Data
public class PasswordUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    public static String encode(String rawPassword) {
        return encoder.encode((CharSequence)rawPassword);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches((CharSequence)rawPassword, encodedPassword);
    }
}
