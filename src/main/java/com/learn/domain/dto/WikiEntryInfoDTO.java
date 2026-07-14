package com.learn.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Schema(description="词条基础信息")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WikiEntryInfoDTO {

    @NotBlank(message="词条标题不能为空！")
    @Schema(description="词条标题")
    private @NotBlank(message="词条标题不能为空！") String title;
    @Schema(description="创建者")
    private String creator;
}
