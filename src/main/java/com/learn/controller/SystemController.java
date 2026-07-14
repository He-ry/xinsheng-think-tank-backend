package com.learn.controller;

import com.learn.domain.pojo.Result;
import com.learn.service.system.SystemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.HashMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="系统")
@RestController
@RequestMapping(value={"/api/v1/system"})
@Validated
public class SystemController {
    @Resource
    private SystemService systemService;

    @GetMapping(value={"/entries-total-count"})
    @Operation(summary="获取词条总数量")
    public Result<HashMap<String, Object>> count() {
        Long count = this.systemService.count();
        HashMap<String, Long> map = new HashMap<String, Long>();
        map.put("totalCount", count);
        return Result.success(map);
    }

    @RequestMapping(value={"/visitor"})
    @Operation(summary="获取访客数量")
    public Result<HashMap<String, Object>> getVisitorCount() {
        HashMap res = this.systemService.getVisitorCount();
        return Result.success((Object)res);
    }

    @PostMapping(value={"/visitor"})
    @Operation(summary="新增进日访客数量")
    public Result<HashMap<String, Object>> increaseVisitor() {
        this.systemService.increaseVisitor();
        return Result.success();
    }
}
