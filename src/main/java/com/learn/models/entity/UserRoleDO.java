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
@TableName(value = "sys_user_role")
public class UserRoleDO {
    @TableId(type=IdType.AUTO)
    private Integer id;
    @TableField(value="user_id")
    private Integer userId;
    @TableField(value="role_id")
    private Integer roleId;
}
