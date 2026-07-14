package com.learn.models.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.autotable.annotation.AutoColumn;
import org.dromara.autotable.annotation.AutoTable;
import org.dromara.autotable.annotation.PrimaryKey;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "sys_role")
@AutoTable(value = "sys_role", comment = "角色表", dialect = "MySQL")
public class RoleDO {
    @PrimaryKey
    @TableId(type=IdType.AUTO)
    private Integer id;
    @AutoColumn(value="name", comment="角色名称", type="varchar", length=30, notNull=true)
    @TableField(value="name")
    private String name;
    @AutoColumn(value="code", comment="角色权限字符串", type="varchar", length=100, notNull=true)
    @TableField(value="code")
    private String code;
    @AutoColumn(value="status", comment="角色状态（0正常 1停用）", type="tinyint", notNull=true)
    @TableField(value="status")
    private Integer status;
}
