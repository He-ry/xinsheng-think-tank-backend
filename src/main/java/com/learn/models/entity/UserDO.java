package com.learn.models.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "sys_user")
public class UserDO {
    @TableId(type=IdType.AUTO)
    private Integer id;
    @TableField(value="username")
    private String username;
    @TableField(value="password")
    private String password;
    @TableField(value="remark")
    private String remark;
    @TableField(value="dept_id")
    private Integer deptId;
    @TableField(value="phone_number")
    private String phoneNumber;
    @TableField(value="avatar")
    private String avatar;
    @TableField(value="is_activate")
    private Boolean isActivate;
    @TableField(value="delete_time")
    private Long deleteTime;
    @TableField(value="experience")
    private Long experience;
    @TableField(value="level")
    private Long level;
    @TableField(value="created_at")
    @Schema(description="创建时间，Unix时间戳，秒级")
    private Long createdAt;
}
