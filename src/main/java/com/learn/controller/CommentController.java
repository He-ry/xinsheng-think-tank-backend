package com.learn.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learn.domain.dto.CommentQueryDTO;
import com.learn.domain.dto.CommentSaveDTO;
import com.learn.domain.pojo.PageResult;
import com.learn.domain.pojo.Result;
import com.learn.domain.vo.CommentSaveVO;
import com.learn.domain.vo.CommentTreeVO;
import com.learn.service.comment.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.HashMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="评论")
@RestController
@RequestMapping(value={"/api/v1/comments"})
@Validated
public class CommentController {
    @Resource
    private CommentService commentService;

    @PostMapping(value={"/add"})
    public Result<CommentSaveVO> add(@RequestBody @Validated CommentSaveDTO dto) {
        CommentSaveVO vo = this.commentService.add(dto);
        return Result.success((Object)vo);
    }

    @GetMapping(value={"/list"})
    public Result<PageResult<CommentTreeVO>> listComments(@RequestParam(value="entry_id") String entryId, @RequestParam(value="parent_id", required=false) String parentId, @RequestParam(value="sort", required=false, defaultValue="newest") String sort, @RequestParam(value="page", required=false, defaultValue="1") Integer page, @RequestParam(value="per_page", required=false, defaultValue="20") Integer perPage) {
        CommentQueryDTO dto = CommentQueryDTO.builder().entryId(entryId).parentId(parentId).sort(sort).page(page).perPage(perPage).build();
        Page commentTreeVOPage = this.commentService.listComments(dto);
        PageResult pageResult = new PageResult(commentTreeVOPage.getRecords(), Long.valueOf(commentTreeVOPage.getTotal()), Long.valueOf(commentTreeVOPage.getCurrent()), Long.valueOf(commentTreeVOPage.getPages()));
        return Result.success((Object)pageResult);
    }

    @DeleteMapping(value={"/remove"})
    public Result<String> remove(@RequestParam(value="id") String id) {
        this.commentService.deleteComment(id);
        return Result.success();
    }

    @GetMapping(value={"/like"})
    public Result<HashMap<String, Object>> like(@RequestParam(value="id") String id, @RequestParam(value="id_add") Boolean idAdd) {
        HashMap res = this.commentService.like(id, idAdd);
        return Result.success((Object)res);
    }

    @GetMapping(value={"/dislike"})
    public Result<HashMap<String, Object>> dislike(@RequestParam(value="id") String id, @RequestParam(value="id_add") Boolean idAdd) {
        HashMap dislike = this.commentService.dislike(id, idAdd);
        return Result.success((Object)dislike);
    }
}
