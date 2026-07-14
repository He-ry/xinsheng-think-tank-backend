package com.learn.domain.vo.maxkb;

import com.baomidou.mybatisplus.annotation.TableField;
import com.learn.models.bo.CitationBO;
import com.learn.models.bo.ContentBO;
import com.learn.models.bo.RelatedEntryBO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaxKbWikiContentVO {

    @Schema(description="词条唯一ID，UUID")
    private String id;
    @Schema(description="词条标题，唯一")
    private String title;
    @Schema(description="规范化URL路径，用于页面路由")
    private String canonicalUrl;
    @Schema(description="词条状态")
    @TableField(value="status")
    private String state;
    @Schema(description="词条状态的消息")
    @TableField(value="statusMessage")
    private String stateMessage;
    @Schema(description="创建时间，Unix时间戳（秒）")
    private Long createAt;
    @Schema(description="修改时间，Unix时间戳（秒）")
    private Long updateAt;
    @Schema(description="创建者")
    private String creator;
    @Schema(description="创建者姓名")
    @TableField(value="creatorId")
    private String creatorName;
    @Schema(description="总字数统计")
    private Long totalWords;
    @Schema(description="总引用数量")
    @TableField(value="totalCitationsNum")
    private Integer totalCitationsNum;
    @Schema(description="章节数量统计数组，如 [一级, 二级, 三级]")
    private List<Long> chaptersNum;
    @Schema(description="内容BO")
    @TableField(value="content")
    private ContentBO content;
    @Schema(description="引用来源列表")
    @TableField(value="citations")
    private List<CitationBO> citations;
    @Schema(description="相关词条列表")
    @TableField(value="related_entries")
    private List<RelatedEntryBO> relatedEntries;
}
