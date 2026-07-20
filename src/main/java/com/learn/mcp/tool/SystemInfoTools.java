package com.learn.mcp.tool;

import com.learn.service.system.SystemService;
import jakarta.annotation.Resource;
import java.util.HashMap;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;

@Component
public class SystemInfoTools {

    @Resource
    private SystemService systemService;

    @McpTool(description = "获取系统运行统计信息，包括已发布词条总数、今日访客数和累计访客数。")
    public HashMap<String, Object> getSystemStats() {
        long count = systemService.count();
        HashMap<String, Object> visitorCount = systemService.getVisitorCount();
        HashMap<String, Object> result = new HashMap<>();
        result.put("totalEntries", count);
        result.put("todayVisitors", visitorCount.getOrDefault("today", 0L));
        result.put("totalVisitors", visitorCount.getOrDefault("total", 0L));
        return result;
    }
}
