package com.learn.models.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Schema(description="词条章节信息")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionBO {

    @Schema(description="章节ID")
    private String id;
    @NotBlank
    @Schema(description="章节标题")
    private String heading;
    @Schema(description="章节内容（Markdown）")
    private String content;
    @NotNull
    @Schema(description="章节层级")
    private Integer level;
    @Valid
    @Schema(description="子章节")
    private List<SectionBO> children;
}
