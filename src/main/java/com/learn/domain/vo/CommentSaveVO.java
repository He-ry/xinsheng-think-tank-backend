package com.learn.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentSaveVO {

    @TableId(value="id")
    @Schema(description="评论唯一ID")
    private String id;
    @TableField(value="parent_id")
    @Schema(description="父评论ID，顶级评论为NULL")
    private String parentId;
    @TableField(value="root_id")
    @Schema(description="根评论ID，无论层级，指向最顶级评论")
    private String rootId;
    @TableField(value="entry_id")
    @Schema(description="词条ID")
    private String entryId;
    @TableField(value="content")
    @Schema(description="评论内容")
    private String content;
    @TableField(value="user_id")
    @Schema(description="用户ID")
    private String userId;
    @TableField(value="user_name")
    @Schema(description="用户名")
    private String userName;
    @TableField(value="like_count")
    @Schema(description="点赞数")
    private Integer likeCount;
    @TableField(value="reply_count")
    @Schema(description="子评论数")
    private Integer replyCount;
    @TableField(value="created_at")
    @Schema(description="创建时间，Unix时间戳，秒级")
    private Long createdAt;
}
