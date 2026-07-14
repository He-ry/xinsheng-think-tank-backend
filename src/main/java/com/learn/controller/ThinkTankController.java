package com.learn.controller;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.learn.domain.dto.WikiSaveDTO;
import com.learn.domain.pojo.PageResult;
import com.learn.domain.pojo.Result;
import com.learn.domain.vo.IdVO;
import com.learn.domain.vo.WikiContentRemoveVO;
import com.learn.domain.vo.WikiContentVO;
import com.learn.domain.vo.WikiListVO;
import com.learn.models.entity.ThinkTankDO;
import com.learn.models.enums.WikiStateEnum;
import com.learn.service.thinktank.ThinkTankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="百科词条管理")
@RestController
@RequestMapping(value={"/api/v1/wiki"})
@Validated
public class ThinkTankController {
    @Resource
    private ThinkTankService thinkTankService;

    @PostMapping(value={"/entry"})
    @Operation(summary="创建或更新词条")
    public Result<IdVO> save(@RequestBody @Valid WikiSaveDTO dto) {
        String id = this.thinkTankService.createThinkTank(dto);
        return Result.success(new IdVO(id));
    }

    @GetMapping(value={"/entry"})
    @Operation(summary="获取词条详情")
    public Result<WikiContentVO> detail(@RequestParam(value="title", required=false) String title, @RequestParam(value="id", required=false) String id) {
        WikiContentVO wikiContentVO = this.thinkTankService.getThinkTank(title, id);
        return Result.success(wikiContentVO);
    }

    @GetMapping(value={"/entries"})
    @Operation(summary="获取词条条目列表")
    public Result<PageResult<WikiListVO>> list(@RequestParam(value="page", required=false, defaultValue="1") String pageNum, @RequestParam(value="per_page", required=false, defaultValue="10") String pageSize, @RequestParam(value="sort_by", required=false, defaultValue="update_at") String sortBy, @RequestParam(value="sort_order", required=false, defaultValue="desc") String order, @RequestParam(value="status", required=false, defaultValue="all") String status) {
        List<String> statusList = WikiStateEnum.ALL.getCode().equals(status) ? WikiStateEnum.getAllState() : List.of(status);
        PageResult thinkTankList = this.thinkTankService.getThinkTankList(pageNum, pageSize, sortBy, order, statusList, null, null);
        return Result.success(thinkTankList);
    }

    @DeleteMapping(value={"/entry"})
    @Operation(summary="删除词条")
    public Result<WikiContentRemoveVO> delete(@RequestParam(value="id") String id) {
        WikiContentRemoveVO wikiContentRemoveVO = this.thinkTankService.deleteThinkTank(id);
        return Result.success(wikiContentRemoveVO);
    }

    @GetMapping(value={"/add-view"})
    @Operation(summary="增加查看数")
    public Result<String> addView(@RequestParam(value="id") String id) {
        this.thinkTankService.addView(id);
        return Result.success();
    }

    @GetMapping(value={"/count"})
    @Operation(summary="获取词条数量")
    public Result<Long> count() {
        long count = ((LambdaQueryChainWrapper)this.thinkTankService.lambdaQuery().eq(ThinkTankDO::getState, (Object)WikiStateEnum.PUBLISHED.getCode())).count();
        return Result.success(count);
    }
}
