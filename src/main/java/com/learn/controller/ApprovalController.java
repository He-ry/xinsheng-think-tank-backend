package com.learn.controller;

import com.learn.domain.dto.ThinkTankApprovalDTO;
import com.learn.domain.pojo.Result;
import com.learn.service.thinktank.ThinkTankService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="审批")
@RestController
@RequestMapping(value={"/api/v1/manage"})
@Validated
public class ApprovalController {
    @Resource
    private ThinkTankService thinkTankService;

    @RequestMapping(value={"/approval"})
    public Result<String> approval(@RequestBody ThinkTankApprovalDTO dto) {
        this.thinkTankService.approval(dto);
        return Result.success();
    }
}
