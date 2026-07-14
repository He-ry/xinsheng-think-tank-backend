package com.learn.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Schema(name="CommentTreeVO", description="评论树形结构")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentTreeVO {

    @Schema(description="评论唯一ID")
    private String id;
    @Schema(description="父评论ID")
    private String parentId;
    @Schema(description="根评论ID")
    private String rootId;
    @Schema(description="词条ID")
    private String entryId;
    @Schema(description="评论内容")
    private String content;
    @Schema(description="用户ID")
    private String userId;
    @Schema(description="isAuthor")
    private Boolean isAuthor;
    @Schema(description="用户名")
    private String userName;
    @Schema(description="点赞数")
    private Integer likeCount;
    @Schema(description="点踩数")
    private Integer dislikeCount;
    @Schema(description="子评论数")
    private Integer replyCount;
    @Schema(description="创建时间，Unix时间戳")
    private Long createdAt;
    @Schema(description="子评论列表")
    private List<CommentTreeVO> children;
}
