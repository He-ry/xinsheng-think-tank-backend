package com.learn.models.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Schema(description="相关词条信息")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatedEntryBO {

    @Schema(description="词条ID")
    private String id;
    @Schema(description="词条标题")
    private String title;
    @Schema(description="规范化URL路径")
    private String canonicalUrl;
}
