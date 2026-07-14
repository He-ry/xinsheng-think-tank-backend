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
import org.dromara.core.trans.vo.TransPojo;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_system")
@Schema(name = "ThinkTankDO", description = "系统信息表")
@JsonIgnoreProperties(value = {"transMap"})
public class SystemDO
implements Serializable,
TransPojo {
    private static final long serialVersionUID = 1L;
    @Schema(description="id")
    @TableId(value="id")
    private String id;
    @Schema(description="sys_key")
    @TableField(value="sys_key")
    private String sysKey;
    @Schema(description="value")
    @TableField(value="value")
    private String value;
    @Schema(description="描述")
    @TableField(value="description")
    private String description;
}
