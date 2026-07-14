package com.learn.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentSaveDTO {

    @Schema(description="评论的ID，UUID格式", required=true, example="b4484e3f-dfd2-4614-a644-dad4cef810e8")
    @NotBlank(message="id不能为空")
    private @NotBlank(message="id不能为空") String id;
    @Schema(description="父评论ID，UUID格式，顶级评论可为空", example="b4484e3f-dfd2-4614-a644-dad4cef810e8")
    @JsonProperty(value="parent_id")
    private String parentId;
    @Schema(description="词条ID，可为空", example="b4484e3f-dfd2-4614-a644-dad4cef810e8")
    @NotBlank(message="词条ID不能为空")
    @JsonProperty(value="entry_id")
    private @NotBlank(message="词条ID不能为空") String entryId;
    @Schema(description="评论内容，富文本HTML", required=true)
    @NotBlank(message="评论内容不能为空")
    private @NotBlank(message="评论内容不能为空") String content;
    @Schema(description="用户ID，随机生成", required=true, example="123456")
    @JsonProperty(value="user_id")
    private String userId;
}
