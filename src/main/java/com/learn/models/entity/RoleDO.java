package com.learn.models.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "sys_role")
public class RoleDO {
    @TableId(type=IdType.AUTO)
    private Integer id;
    @TableField(value="name")
    private String name;
    @TableField(value="code")
    private String code;
    @TableField(value="status")
    private Integer status;
}
