package com.learn.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Schema(name="CommentQueryDTO", description="查询评论参数")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentQueryDTO {

    @Schema(description="词条UUID", required=true)
    @NotBlank
    private String entryId;
    @Schema(description="父评论UUID，为空则获取顶级评论")
    private String parentId;
    @Schema(description="排序方式: newest, oldest, hot")
    private String sort;
}
