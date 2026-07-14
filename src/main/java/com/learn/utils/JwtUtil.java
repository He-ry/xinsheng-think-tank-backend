package com.learn.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
    @Value(value="${jwt.secret}")
    private String SECRET;
    @Value(value="${jwt.access-token.default-expire-seconds}")
    private long DEFAULT_EXPIRE_SECONDS;
    @Value(value="${jwt.refresh-token.default-expire-seconds}")
    private long REFRESH_EXPIRE_SECONDS;

    public String generate(Integer userId, String username, String role, long expireSeconds) {
        Instant now = Instant.now();
        Date from = Date.from(now.plusSeconds(expireSeconds));
        log.info("generate token expire in {} seconds", (Object)expireSeconds);
        return Jwts.builder().setSubject(String.valueOf(userId)).claim("username", (Object)username).claim("role", (Object)role).setIssuedAt(Date.from(now)).setExpiration(from).signWith((Key)Keys.hmacShaKeyFor((byte[])this.SECRET.getBytes(StandardCharsets.UTF_8))).compact();
    }


    public Claims parse(String token) {
        return (Claims)Jwts.parserBuilder().setSigningKey((Key)Keys.hmacShaKeyFor((byte[])this.SECRET.getBytes(StandardCharsets.UTF_8))).setAllowedClockSkewSeconds(30L).build().parseClaimsJws(token).getBody();
    }


    public Integer getUserId(String token) {
        return Integer.valueOf(this.parse(token).getSubject());
    }
}
