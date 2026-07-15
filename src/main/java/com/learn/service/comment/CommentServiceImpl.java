package com.learn.service.comment;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learn.domain.dto.CommentQueryDTO;
import com.learn.domain.dto.CommentSaveDTO;
import com.learn.domain.vo.CommentSaveVO;
import com.learn.domain.vo.CommentTreeVO;
import com.learn.exception.ServiceException;
import com.learn.models.entity.CommentDO;
import com.learn.models.entity.ThinkTankDO;
import com.learn.models.mapper.CommentMapper;
import com.learn.service.comment.CommentService;
import com.learn.service.thinktank.ThinkTankService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class CommentServiceImpl
extends ServiceImpl<CommentMapper, CommentDO>
implements CommentService {
    private static final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private ThinkTankService thinkTankService;

    @Transactional(rollbackFor={Exception.class})
    public CommentSaveVO add(CommentSaveDTO dto) {
        CommentDO comment = CommentDO.builder()
            .id(dto.getId())
            .parentId(dto.getParentId())
            .entryId(dto.getEntryId())
            .content(dto.getContent())
            .userId(dto.getUserId())
            .userName(dto.getUserName())
            .likeCount(0)
            .replyCount(0)
            .createdAt(System.currentTimeMillis() / 1000L)
            .build();
        ThinkTankDO byId = (ThinkTankDO) this.thinkTankService.getById(dto.getEntryId());
        if (byId == null) {
            throw new ServiceException("词条不存在");
        }
        if (dto.getParentId() == null) {
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            comment.setRootId(uuid);
            comment.setId(uuid);
            this.commentMapper.insert(comment);
        } else {
            CommentDO parent = this.commentMapper.selectById(dto.getParentId());
            if (parent == null) {
                throw new ServiceException("父评论不存在: " + dto.getParentId());
            }
            if (parent.getParentId() == null) {
                comment.setRootId(parent.getId());
            } else {
                comment.setRootId(parent.getRootId());
            }
            this.commentMapper.update(null, new LambdaUpdateWrapper<CommentDO>().eq(CommentDO::getId, parent.getId()).setSql("reply_count = reply_count + 1"));
            this.commentMapper.insert(comment);
        }
        return CommentSaveVO.builder()
            .id(comment.getId())
            .parentId(comment.getParentId())
            .rootId(comment.getRootId())
            .entryId(comment.getEntryId())
            .content(comment.getContent())
            .userId(comment.getUserId())
            .userName(comment.getUserName())
            .likeCount(comment.getLikeCount())
            .replyCount(comment.getReplyCount())
            .createdAt(comment.getCreatedAt())
            .build();
    }

    @Transactional(readOnly=true)
    public Page<CommentTreeVO> listComments(CommentQueryDTO dto) {
        ThinkTankDO byId = (ThinkTankDO) this.thinkTankService.getById(dto.getEntryId());
        LambdaQueryWrapper<CommentDO> query = new LambdaQueryWrapper<CommentDO>()
            .eq(CommentDO::getEntryId, dto.getEntryId());
        if (dto.getParentId() == null) {
            query.isNull(CommentDO::getParentId);
        } else {
            query.eq(CommentDO::getParentId, dto.getParentId());
        }
        switch (dto.getSort().toLowerCase()) {
            case "newest":
                query.orderByDesc(CommentDO::getCreatedAt);
                break;
            case "oldest":
                query.orderByAsc(CommentDO::getCreatedAt);
                break;
            case "hot":
                query.orderByDesc(CommentDO::getLikeCount);
                break;
        }
        Page<CommentDO> page = new Page<>(dto.getPage(), dto.getPerPage());
        Page<CommentDO> resultPage = this.commentMapper.selectPage(page, query);
        if (resultPage.getRecords().isEmpty()) {
            return new Page<>();
        }
        List<String> rootIds = resultPage.getRecords().stream().map(CommentDO::getId).toList();
        List<CommentDO> childComments = this.lambdaQuery()
            .eq(CommentDO::getEntryId, dto.getEntryId())
            .in(CollUtil.isNotEmpty(rootIds), CommentDO::getRootId, rootIds)
            .and(item -> item.ne(CommentDO::getParentId, null).or().ne(CommentDO::getParentId, ""))
            .orderByAsc(CommentDO::getCreatedAt)
            .list();
        HashMap<String, CommentTreeVO> voMap = new HashMap<>();
        List<CommentTreeVO> topLevelVos = resultPage.getRecords().stream().map(comment -> {
            CommentTreeVO vo = CommentTreeVO.builder()
                .id(comment.getId())
                .isAuthor(comment.getUserId().equals(byId == null ? "" : byId.getCreator()))
                .parentId(comment.getParentId())
                .rootId(comment.getRootId())
                .entryId(comment.getEntryId())
                .content(comment.getContent())
                .userId(comment.getUserId())
                .userName(comment.getUserName())
                .likeCount(comment.getLikeCount())
                .dislikeCount(comment.getDislikeCount())
                .replyCount(comment.getReplyCount())
                .createdAt(comment.getCreatedAt())
                .children(new ArrayList<>())
                .build();
            voMap.put(vo.getId(), vo);
            return vo;
        }).toList();
        for (CommentDO comment2 : childComments) {
            CommentTreeVO vo = CommentTreeVO.builder()
                .id(comment2.getId())
                .parentId(comment2.getParentId())
                .rootId(comment2.getRootId())
                .isAuthor(comment2.getUserId().equals(byId == null ? "" : byId.getCreator()))
                .entryId(comment2.getEntryId())
                .content(comment2.getContent())
                .userId(comment2.getUserId())
                .userName(comment2.getUserName())
                .likeCount(comment2.getLikeCount())
                .dislikeCount(comment2.getDislikeCount())
                .replyCount(comment2.getReplyCount())
                .createdAt(comment2.getCreatedAt())
                .children(new ArrayList<>())
                .build();
            CommentTreeVO parent = voMap.get(comment2.getParentId());
            if (parent == null) continue;
            parent.getChildren().add(vo);
        }
        Page<CommentTreeVO> voPage = new Page<>();
        voPage.setCurrent(resultPage.getCurrent());
        voPage.setSize(resultPage.getSize());
        voPage.setTotal(resultPage.getTotal());
        voPage.setRecords(topLevelVos);
        return voPage;
    }

    @Transactional(rollbackFor={Exception.class})
    public void deleteComment(String id) {
        CommentDO comment = this.commentMapper.selectById(id);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        String rootId = comment.getRootId() != null ? comment.getRootId() : comment.getId();
        this.commentMapper.delete(new LambdaQueryWrapper<CommentDO>()
            .eq(CommentDO::getRootId, rootId)
            .or()
            .eq(CommentDO::getId, id));
        if (comment.getParentId() != null) {
            this.updateParentReplyCount(comment.getParentId());
        }
    }

    @Transactional
    public HashMap<String, Object> like(String id, Boolean idAdd) {
        int updated = this.commentMapper.update(null, new LambdaUpdateWrapper<CommentDO>()
            .eq(CommentDO::getId, id)
            .setSql(idAdd ? "like_count = like_count + 1" : "like_count = GREATEST(like_count - 1, 0)"));
        if (updated == 0) {
            throw new RuntimeException("评论不存在");
        }
        CommentDO comment = this.commentMapper.selectById(id);
        HashMap<String, Object> res = new HashMap<>();
        res.put("like_count", comment.getLikeCount());
        res.put("id", id);
        return res;
    }

    private void updateParentReplyCount(String parentId) {
        Long count = this.commentMapper.selectCount(new LambdaQueryWrapper<CommentDO>().eq(CommentDO::getParentId, parentId));
        CommentDO parent = new CommentDO();
        parent.setId(parentId);
        parent.setReplyCount(Integer.parseInt(count.toString()));
        this.commentMapper.updateById(parent);
    }

    @Transactional
    public HashMap<String, Object> dislike(String id, Boolean idAdd) {
        int updated = this.commentMapper.update(null, new LambdaUpdateWrapper<CommentDO>()
            .eq(CommentDO::getId, id)
            .setSql(idAdd ? "dislike_count = dislike_count + 1" : "dislike_count = GREATEST(dislike_count - 1, 0)"));
        if (updated == 0) {
            throw new RuntimeException("评论不存在");
        }
        CommentDO comment = this.commentMapper.selectById(id);
        HashMap<String, Object> res = new HashMap<>();
        res.put("dislike_count", comment.getDislikeCount());
        res.put("id", id);
        return res;
    }
}
