package com.learn.models.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Schema(description="引用来源信息")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CitationBO {

    @Schema(description="引用唯一标识符（数字字符串）")
    private String id;
    @Schema(description="引用标题或来源名称")
    private String title;
    @Schema(description="引用来源URL")
    private String url;
    @Schema(description="引用描述")
    private String description;
    @Schema(description="是否公开", defaultValue="false")
    private Boolean isPublic;
    @Schema(description="图标")
    private String favicon;
}
