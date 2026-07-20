package com.learn.mcp.config;

import com.learn.exception.ServiceException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class McpAuthHelper {

    @Resource
    private McpProperties mcpProperties;

    public boolean isAuthenticated() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra) {
            HttpServletRequest req = sra.getRequest();
            String key = req.getHeader("X-API-Key");
            return key != null && key.equals(mcpProperties.getApiKey());
        }
        return false;
    }

    public void requireAuth() {
        if (!isAuthenticated()) {
            throw new ServiceException("Authentication required. Provide X-API-Key header.");
        }
    }
}
