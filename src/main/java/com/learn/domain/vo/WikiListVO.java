package com.learn.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Schema(description="词条VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WikiListVO {

    @Schema(description="词条唯一ID，UUID")
    private String id;
    @Schema(description="词条标题，唯一")
    private String title;
    @Schema(description="创建者")
    private String creator;
    @Schema(description="修改者")
    private String creatorName;
    @Schema(description="创建时间，Unix时间戳（秒）")
    private Long createAt;
    @Schema(description="修改时间，Unix时间戳（秒）")
    private Long updateAt;
    @Schema(description="查看数")
    private Long view;
    @Schema(description="状态")
    private String state;
    @Schema(description="状态信息")
    private String stateMessage;
    @Schema(description="总字数统计")
    private Long totalWords;
    @Schema(description="章节数量统计数组，如 [一级, 二级, 三级]")
    private List<Long> chaptersNum;
    @Schema(description="引用来源数量")
    private Integer citationNum;
    @Schema(description="相关引用数量")
    private Integer relatedEntriesNum;
}
