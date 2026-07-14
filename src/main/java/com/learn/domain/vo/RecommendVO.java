package com.learn.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendVO {

    @Schema(description="词条标题，唯一")
    private String title;
    @Schema(description="规范化URL路径，用于页面路由")
    private String canonicalUrl;
    @Schema(description="创建者")
    private String creator;
    @Schema(description="摘要")
    private String summary;
    @Schema(description="修改时间，Unix时间戳（秒）")
    private Long updateAt;
    @Schema(description="查看数")
    private Long view;
}
