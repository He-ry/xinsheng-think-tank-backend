package com.learn.models.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_think_tank")
@Schema(name = "ThinkTankDO", description = "百科词条主表")
@JsonIgnoreProperties(value = {"transMap"})
public class ThinkTankDO
implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description="词条唯一ID，UUID")
    @TableId(value="id")
    private String id;
    @Schema(description="词条标题，唯一")
    @TableField(value="title")
    private String title;
    @Schema(description="规范化URL路径，用于页面路由")
    @TableField(value="canonical_url")
    private String canonicalUrl;
    @Schema(description="词条摘要")
    @TableField(value="summary")
    private String summary;
    @Schema(description="创建者")
    @TableField(value="creator")
    private String creator;
    @Schema(description="创建时间，Unix时间戳（秒）")
    @TableField(value="create_at")
    private Long createAt;
    @Schema(description="修改时间，Unix时间戳（秒）")
    @TableField(value="update_at")
    private Long updateAt;
    @Schema(description="查看数")
    @TableField(value="view")
    private Long view;
    @Schema(description="状态")
    @TableField(value="state")
    private String state;
    @Schema(description="状态信息")
    private String stateMessage;
    @Schema(description="总字数统计")
    @TableField(value="total_words")
    private Long totalWords;
    @Schema(description="章节数量统计数组，如 [一级, 二级, 三级]")
    @TableField(value="chapters_num")
    private String chaptersNum;
    @Schema(description="引用来源列表")
    @TableField(value="citations")
    private String citations;
    @Schema(description="相关词条列表")
    @TableField(value="related_entries")
    private String relatedEntries;
    @Schema(description="ai生成词条文件")
    private String files;
}
