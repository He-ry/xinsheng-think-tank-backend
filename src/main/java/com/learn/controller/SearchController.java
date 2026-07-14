package com.learn.controller;

import com.learn.domain.pojo.Result;
import com.learn.domain.vo.RecommendVO;
import com.learn.domain.vo.SearchVO;
import com.learn.service.section.SectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="搜索")
@RestController
@RequestMapping(value={"/api/v1/search"})
@Validated
public class SearchController {
    @Resource
    private SectionService sectionService;

    @GetMapping(value={"random-recommend"})
    @Operation(summary="获取随机推荐")
    public Result<HashMap<String, List<RecommendVO>>> random(@RequestParam(name="num", required=false, defaultValue="5") Integer num) {
        List recommendVO = this.sectionService.random(num);
        HashMap<String, List> map = new HashMap<String, List>();
        map.put("recommend", recommendVO);
        return Result.success(map);
    }

    @GetMapping(value={"/search"})
    @Operation(summary="获取搜索建议")
    public Result<SearchVO> get(@RequestParam(value="search") String search, @RequestParam(name="pageNum", required=false, defaultValue="1") Integer pageNum, @RequestParam(name="per_page", required=false, defaultValue="10") Integer pageSize) {
        SearchVO vo = this.sectionService.searchSuggest(search, pageNum, pageSize);
        return Result.success((Object)vo);
    }
}
