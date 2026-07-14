package com.learn.config.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.learn.config.filter.JwtProperties;
import com.learn.config.filter.TokenBlacklist;
import com.learn.domain.pojo.LoginUser;
import com.learn.domain.pojo.UserContext;
import com.learn.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter
extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final JwtProperties jwtProperties;
    private final AntPathMatcher matcher = new AntPathMatcher();
    @Resource
    private JwtUtil jwtUtil;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        this.addCorsHeader(res, req);
        try {
            Claims parse;
            String uri = req.getRequestURI();
            if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
                chain.doFilter((ServletRequest)req, (ServletResponse)res);
                return;
            }
            if (this.isWhiteList(uri)) {
                chain.doFilter((ServletRequest)req, (ServletResponse)res);
                return;
            }
            String header = req.getHeader("Authorization");
            if (StrUtil.isBlank((CharSequence)header) || !header.startsWith("Bearer ")) {
                this.write401(res, "请先登录！");
                return;
            }
            String token = header.substring(7);
            if (TokenBlacklist.contains((String)token)) {
                log.error("token 已被加入黑名单");
                this.write401(res, "token 无效！");
                return;
            }
            try {
                parse = this.jwtUtil.parse(token);
            }


            catch (JwtException e) {
                this.write401(res, "token 无效！");
                log.error("token解析失败", (Throwable)e);
                UserContext.clear();
                return;
            }
            String sub = parse.getSubject();
            String username = (String)parse.get("username", String.class);
            String role = (String)parse.get("role", String.class);
            if (StrUtil.hasBlank((CharSequence[])new CharSequence[]{sub, username, role})) {
                this.write401(res, "token 无效！");
                log.error("token 请求载荷无效：{}、{}、{}", new Object[]{sub, username, role});
                return;
            }
            LoginUser user = new LoginUser(Integer.valueOf(sub), username, role);
            UserContext.set((LoginUser)user);
            chain.doFilter((ServletRequest)req, (ServletResponse)res);
        }
        finally {
            UserContext.clear();
        }
    }

    private boolean isWhiteList(String uri) {
        return this.jwtProperties.getWhitelist().stream().anyMatch(p -> this.matcher.match(p, uri));
    }

    private void write401(HttpServletResponse res, String msg) throws IOException {
        res.setStatus(401);
        res.setContentType("application/json;charset=UTF-8");
        JSONObject json = JSONUtil.createObj().set("code", (Object)401).set("msg", (Object)msg).set("data", (Object)new JSONObject());
        res.getWriter().write(json.toString());
    }


    private void addCorsHeader(HttpServletResponse res, HttpServletRequest req) {
        res.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
        res.setHeader("Access-Control-Allow-Methods", "*");
        res.setHeader("Access-Control-Allow-Headers", "*");
        res.setHeader("Access-Control-Allow-Credentials", "true");
    }

    public JwtAuthFilter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }
}
