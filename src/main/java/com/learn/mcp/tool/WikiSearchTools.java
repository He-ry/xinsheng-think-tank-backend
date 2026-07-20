package com.learn.mcp.tool;

import com.learn.domain.vo.RecommendVO;
import com.learn.domain.vo.SearchVO;
import com.learn.domain.vo.WikiContentVO;
import com.learn.domain.vo.WikiListVO;
import com.learn.domain.pojo.PageResult;
import com.learn.models.enums.WikiStateEnum;
import com.learn.service.section.SectionService;
import com.learn.service.thinktank.ThinkTankService;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class WikiSearchTools {

    @Resource
    private SectionService sectionService;

    @Resource
    private ThinkTankService thinkTankService;

    @McpTool(description = "全文搜索百科词条，按相关度排序。搜索范围覆盖标题、摘要和章节正文内容。")
    public SearchVO searchWiki(
            @McpToolParam(description = "搜索关键词") String query,
            @McpToolParam(description = "页码，从1开始", required = false) Integer page,
            @McpToolParam(description = "每页条数", required = false) Integer pageSize) {
        int p = page == null || page < 1 ? 1 : page;
        int ps = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 50);
        return sectionService.fulltextSearch(query, p, ps);
    }

    @McpTool(description = "根据标题或ID获取词条完整内容，包含所有章节（Markdown格式）、引用来源和相关词条。")
    public WikiContentVO getWikiEntry(
            @McpToolParam(description = "词条标题（模糊匹配）", required = false) String title,
            @McpToolParam(description = "词条ID（精确匹配）", required = false) String id) {
        return thinkTankService.getThinkTank(title, id);
    }

    @McpTool(description = "分页列出已发布的百科词条，支持排序。")
    public PageResult<WikiListVO> listWikiEntries(
            @McpToolParam(description = "页码，从1开始", required = false) String page,
            @McpToolParam(description = "每页条数", required = false) String pageSize,
            @McpToolParam(description = "排序字段: update_at, create_at, view, total_words", required = false) String sortBy,
            @McpToolParam(description = "排序方向: asc, desc", required = false) String sortOrder) {
        String p = page == null ? "1" : page;
        String ps = pageSize == null ? "10" : pageSize;
        String sb = sortBy == null ? "update_at" : sortBy;
        String so = sortOrder == null ? "desc" : sortOrder;
        return thinkTankService.getThinkTankList(p, ps, sb, so,
                WikiStateEnum.getAllState(), null, null);
    }

    @McpTool(description = "获取已发布词条总数。")
    public Long getWikiCount() {
        return thinkTankService.count();
    }

    @McpTool(description = "获取随机推荐的百科词条，用于知识发现。")
    public List<RecommendVO> getRandomEntries(
            @McpToolParam(description = "推荐数量，默认5，最大10", required = false) Integer count) {
        int c = count == null ? 5 : Math.min(count, 10);
        return sectionService.random(c);
    }
}
