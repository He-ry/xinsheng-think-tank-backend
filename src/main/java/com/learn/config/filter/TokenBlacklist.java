package com.learn.config.filter;

import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;
@Data
public class TokenBlacklist {
    private static final ConcurrentHashMap<String, Long> BLACKLIST = new ConcurrentHashMap();

    public static void add(String token, long expireTime) {
        BLACKLIST.put(token, expireTime);
    }

    public static boolean contains(String token) {
        Long expire = (Long)BLACKLIST.get(token);
        if (expire == null) {
            return false;
        }
        if (System.currentTimeMillis() > expire) {
            BLACKLIST.remove(token);
            return false;
        }
        return true;
    }
}
