package com.learn.service.comment;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learn.domain.dto.CommentQueryDTO;
import com.learn.domain.dto.CommentSaveDTO;
import com.learn.domain.vo.CommentSaveVO;
import com.learn.domain.vo.CommentTreeVO;
import java.util.HashMap;

import lombok.Data;
public interface CommentService {
    public CommentSaveVO add(CommentSaveDTO var1);

    public Page<CommentTreeVO> listComments(CommentQueryDTO var1);

    public void deleteComment(String var1);

    public HashMap<String, Object> like(String var1, Boolean var2);

    public HashMap<String, Object> dislike(String var1, Boolean var2);
}
