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
import org.dromara.autotable.annotation.AutoColumn;
import org.dromara.autotable.annotation.AutoTable;
import org.dromara.autotable.annotation.PrimaryKey;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "sys_user")
@AutoTable(value = "sys_user", comment = "用户信息表", dialect = "MySQL")
public class UserDO {
    @PrimaryKey
    @TableId(type=IdType.AUTO)
    private Integer id;
    @AutoColumn(value="username", comment="用户账号", type="varchar", length=30, notNull=true)
    @TableField(value="username")
    private String username;
    @AutoColumn(value="password", comment="密码", type="varchar", length=100, notNull=true)
    @TableField(value="password")
    private String password;
    @AutoColumn(value="remark", comment="备注", type="varchar", length=500)
    @TableField(value="remark")
    private String remark;
    @AutoColumn(value="dept_id", comment="部门ID", type="bigint")
    @TableField(value="dept_id")
    private Integer deptId;
    @AutoColumn(value="phone_number", comment="手机号码", type="varchar", length=11)
    @TableField(value="phone_number")
    private String phoneNumber;
    @AutoColumn(value="avatar", comment="头像地址", type="varchar", length=512)
    @TableField(value="avatar")
    private String avatar;
    @AutoColumn(value="is_activate", comment="帐号状态（0正常 1停用）", type="tinyint", notNull=true)
    @TableField(value="is_activate")
    private Boolean isActivate;
    @AutoColumn(value="delete_time", comment="预计删除时间", type="bigint")
    @TableField(value="delete_time")
    private Long deleteTime;
    @AutoColumn(value="experience", comment="用户经验值", type="bigint")
    @TableField(value="experience")
    private Long experience;
    @AutoColumn(value="level", comment="用户等级", type="bigint")
    @TableField(value="level")
    private Long level;
    @TableField(value="created_at")
    @Schema(description="创建时间，Unix时间戳，秒级")
    @AutoColumn(value="created_at", comment="创建时间，Unix时间戳，秒级", type="bigint")
    private Long createdAt;
}
