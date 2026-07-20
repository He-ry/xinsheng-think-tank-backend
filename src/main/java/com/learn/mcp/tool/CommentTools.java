package com.learn.mcp.tool;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learn.domain.dto.CommentQueryDTO;
import com.learn.domain.vo.CommentTreeVO;
import com.learn.service.comment.CommentService;
import jakarta.annotation.Resource;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class CommentTools {

    @Resource
    private CommentService commentService;

    @McpTool(description = "获取指定词条的评论列表（树形结构），支持按最新、最早、最热排序。")
    public Page<CommentTreeVO> listComments(
            @McpToolParam(description = "词条ID") String entryId,
            @McpToolParam(description = "页码，从1开始", required = false) Integer page,
            @McpToolParam(description = "每页条数，默认20", required = false) Integer perPage,
            @McpToolParam(description = "排序方式: newest, oldest, hot", required = false) String sort) {
        CommentQueryDTO dto = CommentQueryDTO.builder()
                .entryId(entryId)
                .page(page == null ? 1 : page)
                .perPage(perPage == null ? 20 : Math.min(perPage, 50))
                .sort(sort == null ? "newest" : sort)
                .build();
        return commentService.listComments(dto);
    }
}
