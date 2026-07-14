package com.learn.models.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName(value = "t_section")
@Schema(name = "SectionDO", description = "词条章节表")
public class SectionDO
implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description="章节唯一标识符")
    @TableId(value="id")
    private String id;
    @Schema(description="所属词条ID")
    @TableField(value="think_tank_id")
    private String thinkTankId;
    @Schema(description="父章节ID，一级章节为NULL")
    @TableField(value="parent_id")
    private String parentId;
    @Schema(description="章节标题")
    @TableField(value="heading")
    private String heading;
    @Schema(description="章节内容（Markdown）")
    @TableField(value="content")
    private String content;
    @Schema(description="章节层级，1=一级，2=二级...")
    @TableField(value="level")
    private Integer level;
}
